package com.shu.usercenter2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shu.usercenter2.domain.Course;
import com.shu.usercenter2.domain.User;
import com.shu.usercenter2.service.CourseService;
import com.shu.usercenter2.service.UserService;
import com.shu.usercenter2.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

import static com.shu.usercenter2.constant.userLoginConstant.USER_LOGIN_STATE;

/**
* @author huxing
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-04-14 23:09:51
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Autowired
    private CourseService courseService;

    /**
     * 用户登录，返回用户信息，并将用户信息存入session
     * @param id
     * @param password
     * @param request
     * @return
     */
    @Override
    public User login(int id, String password, HttpServletRequest request) {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id);
        queryWrapper.eq("password",password);
        User user = this.getOne(queryWrapper);
        if(user == null){
            log.info("账号或密码错误");
            return null;
        }

        //登录成功,会话保存用户信息
        request.getSession().setAttribute(USER_LOGIN_STATE,user);

        return user;

    }

    /**
     * 根据姓名，角色添加用户
     * @param name
     * @param role
     * @return
     */
    @Override
    public boolean addUser(String name, Integer role) {

        //先不做规范校验了
        //同名也当新用户
        User user = new User();
        //生成十位初始密码
        String password="0123456789";
        user.setName(name);
        user.setRole(role);
        user.setPassword(password);

        return this.save(user);
    }

    /**
     * 管理员根据课程名称添加课程
     * @param courseName
     * @return
     */
    @Override
    public boolean addCourse(String courseName) {
        //先排查是否有重复课程
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_name", courseName);

        long count = courseService.count(queryWrapper);

        if (count > 0) {
            log.info("已存在相同名称的课程：{}", courseName);
            return false;
        }

        Course new_course = new Course();
        new_course.setCourse_name(courseName);

        return courseService.save(new_course);
    }

    /**
     * 根据姓名，查询角色返回列表
     * @param name
     * @return
     */
    @Override
    public List<User> selectUser(String name) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",name);
        List<User> users = this.list(queryWrapper);
        if(!users.isEmpty()){
            return users;
        }
        log.info("未找到用户：{}",name);
        return Collections.emptyList();
    }

    /**
     * 根据课程名，查询课程
     *
     * @param courseName
     * @param courseId
     * @return
     */
    @Override
    public Course selectCourse(String courseName, Integer courseId) {
        if (StringUtils.isAnyBlank(courseName) && courseId == null) {
            log.info("Either courseName or courseId must be provided.");
            return null;
        }

        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq(courseName != null, "course_name", courseName);
        queryWrapper.eq(courseId != null, "course_id", courseId);

        return courseService.getOne(queryWrapper);

    }
}




