package com.shu.usercenter2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shu.usercenter2.domain.Course;
import com.shu.usercenter2.domain.CourseSchedule;
import com.shu.usercenter2.domain.User;
import com.shu.usercenter2.mapper.CourseMapper;
import com.shu.usercenter2.mapper.CourseScheduleMapper;
import com.shu.usercenter2.service.CourseScheduleService;
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
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CourseScheduleService courseScheduleService;
    @Autowired
    private CourseScheduleMapper courseScheduleMapper;

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
    public Course getCourseInfo(Integer courseId, String courseName) {
        if (courseId == null && StringUtils.isBlank(courseName)) {
            log.info("课程ID和课程名称不能同时为空");
            return null;
        }

        return courseMapper.selectByCourseInfo(courseId, courseName);
    }

    /**
     * 用户查询课程表
     * @param courseId
     * @param semester
     * @param teacherId
     * @param time
     * @param capacity
     * @return
     */
    @Override
    public List<CourseSchedule> selectCourseSchedule(Integer courseId, Integer semester, Integer teacherId,
                                                     String time, Integer capacity) {
        if (courseId == null) {
            log.info("课程ID不能为空");
            return Collections.emptyList();
        }
        return courseScheduleMapper.selectCourseSchedule(courseId, semester, teacherId, time, capacity);
    }

    @Override
    public boolean addCourseSchedule(Integer courseId, Integer semester, Integer teacherId, String time, Integer capacity) {
        //如果有一个参数为空，返回false
        if (courseId == null || semester == null || teacherId == null || StringUtils.isBlank(time) || capacity == null) {
            log.info("参数不能为空");
            return false;
        }
        //如果课表里已经有一个则不添加
        QueryWrapper<CourseSchedule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        queryWrapper.eq("semester", semester);
        queryWrapper.eq("teacher_id", teacherId);
        queryWrapper.eq("time", time);
        queryWrapper.eq("capacity", capacity);
        int count = courseScheduleService.count(queryWrapper);
        if (count > 0) {
            log.info("课表已存在");
            return false;
        }
        CourseSchedule courseSchedule = new CourseSchedule();
        courseSchedule.setCourse_id(courseId);
        courseSchedule.setSemester(semester);
        courseSchedule.setTeacher_id(teacherId);
        courseSchedule.setTime(time);
        courseSchedule.setCapacity(capacity);
        return courseScheduleService.save(courseSchedule);
    }

    /**
     * 用户选课
     * @param courseId
     * @param semester
     * @param teacherId
     * @param studentId
     * @param time
     * @param capacity
     * @return
     */
//    @Override
//    public boolean courseSelect(Integer courseId, Integer semester,
//                                Integer teacherId, String time,
//                                Integer capacity, Integer studentId) {
//        //先检查课程是否存在,这里应该查课程表，因为课程存在不一定课程表就有
//        QueryWrapper<CourseSchedule> wrapper = new QueryWrapper<>();
//        wrapper.eq("course_id", courseId);
//        wrapper.eq("semester", semester);
//        wrapper.eq("teacher_id", teacherId);
//        wrapper.eq("time", time);
//
//        if (course == null) {
//            log.info("课程不存在");
//            return false;
//        }
//        //检查课程是否已满
//        if (course.getCapacity() <= 0) {
//            log.info("课程已满");
//            return false;
//        }
//        //检查学生是否已选过该课程
//        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
//        return false;
//    }


}




