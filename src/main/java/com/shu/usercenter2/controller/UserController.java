package com.shu.usercenter2.controller;

import com.shu.usercenter2.domain.*;
import com.shu.usercenter2.request.UserLoginRequest;
import com.shu.usercenter2.request.UserSelectCourseScheduleRequest;
import com.shu.usercenter2.request.UserUpdateScoreRequest;
import com.shu.usercenter2.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static com.shu.usercenter2.constant.userLoginConstant.USER_LOGIN_STATE;

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
     * 管理员添加课表
     * @param courseSchedule
     * @return
     */
    @GetMapping("/addCourseSchedule")
    public boolean addCourseSchedule(@RequestBody CourseSchedule courseSchedule, HttpServletRequest request) {
        //取得角色身份
        Object o = request.getSession().getAttribute(USER_LOGIN_STATE);
        if(o!=null){
            User cur_user=(User) o;
            Integer role = cur_user.getRole();
            if(role==0){
                Integer semester = courseSchedule.getSemester();
                Integer courseId = courseSchedule.getCourse_id();
                Integer teacherId = courseSchedule.getTeacher_id();
                String time = courseSchedule.getTime();
                Integer capacity = courseSchedule.getCapacity();
                return userService.addCourseSchedule(courseId, semester, teacherId,time,capacity);
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

    /**
     * 用户对课表查询
     * @param courseSchedule
     */
    @GetMapping("/selectCourseSchedule")
    public List<CourseSchedule> selectCourseSchedule(@RequestBody CourseSchedule courseSchedule,
                                                     HttpServletRequest httpServletRequest){
        Object o = httpServletRequest.getSession().getAttribute(USER_LOGIN_STATE);
        if(o!=null){
            Integer semester = courseSchedule.getSemester();
            Integer courseId = courseSchedule.getCourse_id();
            Integer teacherId = courseSchedule.getTeacher_id();
            String time = courseSchedule.getTime();
            Integer capacity = courseSchedule.getCapacity();
            return userService.selectCourseSchedule(courseId, semester, teacherId,time,capacity);
        }
        else{
            log.info("会话过期，请重新登录");
            return null;
        }
    }

    /**
     * 学生或者管理员选课,如果是学生就用会话的学生id信息,传参为null，如果是管理员就用传入的学生id信息，不能为null
     * @return
     */
    @GetMapping("/courseSelect")
    public boolean courseSelect(@RequestBody UserSelectCourseScheduleRequest courseSchedule,
                                HttpServletRequest httpServletRequest){
        Object o = httpServletRequest.getSession().getAttribute(USER_LOGIN_STATE);
        if(o!=null) {
            User user = (User) o;
            if (user.getRole() == 2) {
                log.info("教师无法选课");
                return false;
            }
            Integer courseId = courseSchedule.getCourse_id();
            Integer semester = courseSchedule.getSemester();
            Integer teacherId = courseSchedule.getTeacher_id();
            String time = courseSchedule.getTime();
            Integer capacity = courseSchedule.getCapacity();
            Integer studentId = courseSchedule.getStudent_id();

            if (user.getRole() == 0) {
                if (studentId == null) {
                    log.info("管理员选课需要传入学生id");
                    return false;
                }
            }
            if (user.getRole() == 1) studentId = user.getId();

            return userService.courseSelect(courseId, semester, teacherId,time,capacity, studentId);
        }
        else{
            log.info("会话过期，请重新登录");
            return false;
        }
    }

    /**
     * 学生或管理员查询学生某学期的选课情况,用的是courseSelection查询，返回的是一个课程安排列表
     */
    @GetMapping("/semesterCourseSelection")
    public List<CourseSchedule> semesterCourseSelection(@RequestBody CourseSelection courseSelection,
                                                         HttpServletRequest httpServletRequest){
        Object o = httpServletRequest.getSession().getAttribute(USER_LOGIN_STATE);
        if(o!=null){
            User user = (User) o;
            Integer studentId = courseSelection.getStudent_id();
            Integer semester = courseSelection.getSemester();
            if(user.getRole()==0){
                if(studentId==null){
                    log.info("管理员查询学生选课情况需要传入学生id");
                    return null;
                }
            }
            if(user.getRole()==1) studentId = user.getId();
            return userService.semesterCourseSelection(studentId,semester);
        }
        else{
            log.info("会话过期，请重新登录");
            return null;
        }
    }

    /**
     * 学生或管理员退课
     */
    @GetMapping("/courseWithdraw")
    public boolean courseWithdraw(@RequestBody UserSelectCourseScheduleRequest courseSchedule,
                                  HttpServletRequest httpServletRequest){
        Object o = httpServletRequest.getSession().getAttribute(USER_LOGIN_STATE);
        if(o!=null) {
            User user = (User) o;
            if (user.getRole() == 2) {
                log.info("教师无法退课");
                return false;
            }
            Integer courseId = courseSchedule.getCourse_id();
            Integer semester = courseSchedule.getSemester();
            Integer teacherId = courseSchedule.getTeacher_id();
            String time = courseSchedule.getTime();
            Integer capacity = courseSchedule.getCapacity();
            Integer studentId = courseSchedule.getStudent_id();

            if (user.getRole() == 0) {
                if (studentId == null) {
                    log.info("管理员退课需要传入学生id");
                    return false;
                }
            }
            if (user.getRole() == 1) studentId = user.getId();

            return userService.courseWithdraw(courseId, semester, teacherId,time,capacity, studentId);
        }
        else{
            log.info("会话过期，请重新登录");
            return false;
        }
    }

    /**
     * 教师或管理员更新成绩
     */
    @PutMapping("/updateScore")
    public boolean updateScore(@RequestBody UserUpdateScoreRequest score, HttpServletRequest httpServletRequest){
        Object o = httpServletRequest.getSession().getAttribute(USER_LOGIN_STATE);
        if(o!=null){
            User user = (User) o;
            if(user.getRole()==1) {
                log.info("学生不能登入成绩");
                return false;
            }
            Integer courseId = score.getCourse_id();
            Integer semester = score.getSemester();
            Integer studentId = score.getStudent_id();
            Integer newScore = score.getNew_score();
            return userService.updateScore(courseId,semester,studentId,newScore);

        }
        else{
            log.info("会话过期，请重新登录");
            return false;
        }
    }

    /**
     * 教师或管理员添加学生成绩
     */
    @GetMapping("/addScore")
    public boolean addScore(@RequestBody Score score, HttpServletRequest httpServletRequest){
        Object o = httpServletRequest.getSession().getAttribute(USER_LOGIN_STATE);
        if(o!=null){
            User user = (User) o;
            if(user.getRole()==1) {
                log.info("学生不能添加成绩");
                return false;
            }
            Integer courseId = score.getCourse_id();
            Integer semester = score.getSemester();
            Integer studentId = score.getStudent_id();
            Integer score1 = score.getScore();
            return userService.addScore(courseId,semester,studentId,score1);

        }
        else{
            log.info("会话过期，请重新登录");
            return false;
        }
    }

    /**
     * 用户查看成绩
     */

}
