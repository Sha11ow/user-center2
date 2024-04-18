package com.shu.usercenter2.controller;

import com.shu.usercenter2.domain.Course;
import com.shu.usercenter2.domain.User;
import com.shu.usercenter2.request.UserLoginRequest;
import com.shu.usercenter2.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static com.shu.usercenter2.constant.userLoginConstant.USER_LOGIN_STATE;
import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录,返回用户信息，并将用户信息存入session
     * @param userLoginRequest
     * @param request
     * @return
     */
    @RequestMapping("/login")
    public User login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {

        if (userLoginRequest==null){
            return null;
        }
        int id = userLoginRequest.getId();
        String password = userLoginRequest.getPassword();
        if(id <= 0 || StringUtils.isBlank(password)){
            return null;
        }
        return userService.login(id, password, request);
    }

    /**
     * 用户登出
     * @param request
     */
    @RequestMapping("/logout")
    public void logout(HttpServletRequest request) {
        request.getSession().removeAttribute("USER_LOGIN_STATE");
    }

    //====================================================================================================
    //管理员部分

    /**
     * 管理员根据姓名，角色增加用户
     * @param user
     * @return
     */
    @GetMapping("/addUser")
    public boolean addUser(@RequestBody User user,HttpServletRequest request) {
        //取得角色身份
        Object o = request.getSession().getAttribute(USER_LOGIN_STATE);
        if(o!=null){
            User cur_user=(User) o;
            Integer role = cur_user.getRole();
            if(role==0){
                String add_name = user.getName();
                Integer add_role = user.getRole();
                return userService.addUser(add_name,add_role);
            }
            else {
                log.info("不是管理员");
                return false;
            }

        }
        else {
            log.info("会话过期，令牌已失效");
            return false;
        }

    }

    /**
     * 管理员添加课程，若课程已存在会报错
     *
     */
    @GetMapping("/addCourse")
    public boolean addCourse(@RequestBody Course course, HttpServletRequest request) {
        //取得角色身份
        Object o = request.getSession().getAttribute(USER_LOGIN_STATE);
        if(o!=null){
            User cur_user=(User) o;
            Integer role = cur_user.getRole();
            if(role==0){
                String courseName = course.getCourse_name();
                return userService.addCourse(courseName);
            }
            else {
                log.info("不是管理员");
                return false;
            }

        }
        else {
            log.info("会话过期，令牌已失效");
            return false;
        }

    }

    /**
     * 管理员查询用户,返回的是一个用户列表（同名同姓）
     * @param user
     * @return
     */
    @GetMapping("/selectUser")
    public List<User> selectUser(@RequestBody User user, HttpServletRequest request) {
        //取得角色身份
        Object o = request.getSession().getAttribute(USER_LOGIN_STATE);
        if(o!=null){
            User cur_user=(User) o;
            Integer role = cur_user.getRole();
            if(role==0){
                String name = user.getName();
                return userService.selectUser(name);
            }
            else {
                log.info("不是管理员");
                return null;
            }

        }
        else {
            log.info("会话过期，令牌已失效");
            return null;
        }

    }

    //====================================================================================================
    /**
     * 用户查寻课程
     * @param course
     * @return
     */
    @GetMapping("/selectCourse")
    public Course selectCourse(@RequestBody Course course, HttpServletRequest httpServletRequest){
        Object o = httpServletRequest.getSession().getAttribute(USER_LOGIN_STATE);
        if(o!=null){
            Integer courseId = course.getCourse_id();
            String courseName = course.getCourse_name();
            return userService.getCourseInfo(courseId,courseName);
        }
        else{
            log.info("会话过期，请重新登录");
            return null;
        }
    }


}
