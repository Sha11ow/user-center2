package com.shu.usercenter2.service;

import com.shu.usercenter2.domain.*;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author huxing
* @description 针对表【user】的数据库操作Service
* @createDate 2024-04-14 23:09:51
*/
public interface UserService extends IService<User> {

    /**
     *用户登录，返回用户信息，并将用户信息存入session
     * @param id
     * @param password
     * @param request
     * @return
     */
    User login(int id, String password, HttpServletRequest request);

    /**
     * 根据姓名，角色添加用户
     * @param name
     * @param role
     */
    boolean addUser(String name, Integer role);

    /**
     * 根据课程名，添加课程
     * @param courseName
     * @return
     */
    boolean addCourse(String courseName);

    /**
     * 根据姓名，查询角色返回列表
     * @param name
     * @return
     */
    List<User> selectUser(String name);

    /**
     * 根据课程名或者课程号查询课程
     *
     * @param courseName
     * @param courseId
     * @return
     */
    Course getCourseInfo(Integer courseId, String courseName);

    /**
     * 用户查询课程表
     * @param courseId
     * @param semester
     * @param teacherId
     * @param time
     * @param capacity
     * @return
     */
    List<CourseSchedule> selectCourseSchedule(Integer courseId, Integer semester,
                                              Integer teacherId, String time, Integer capacity);

    /**
     * 添加课程表
     * @param courseId
     * @param semester
     * @param teacherId
     * @param time
     * @param capacity
     * @return
     */
    boolean addCourseSchedule(Integer courseId, Integer semester, Integer teacherId, String time, Integer capacity);


    /**
     * 用户选课
     * @param courseId
     * @param semester
     * @param teacherId
     * @param studentId
     * @param time
     * @return
     */
    boolean courseSelect(Integer courseId, Integer semester, Integer teacherId, String time,Integer capacity,
                          Integer studentId);


    /**
     * 学生学期课表查询
     * @param semester
     * @param studentId
     * @return
     */
    List<CourseSchedule> semesterCourseSelection(Integer studentId, Integer semester);

    /**
     * 学生退课
     * @param courseId
     * @param semester
     * @param teacherId
     * @param time
     * @param capacity
     * @param studentId
     * @return
     */
    boolean courseWithdraw(Integer courseId, Integer semester, Integer teacherId, String time, Integer capacity, Integer studentId);

    /**
     * 更新成绩
     * @param courseId
     * @param semester
     * @param studentId
     * @param newScore
     * @return
     */
    boolean updateScore(Integer courseId, Integer semester, Integer studentId,Integer newScore);

    /**
     * 根据课程ID、学期和学生ID获取单个Score对象。
     *
     * @param courseId   课程ID
     * @param semester   学期
     * @param studentId  学生ID
     * @return 匹配的Score对象，如果没有找到则返回null
     */
    boolean addScore(Integer courseId, Integer semester, Integer studentId, Integer score1);

    /**
     * 根据学生ID和学期查询成绩
     * @param studentId
     * @param semester
     * @return
     */
    List<Score> selectScore(Integer studentId, Integer semester);

    /**
     * 根据学生ID和学期查询学生成绩
     * @param teacherId
     * @param semester
     * @return
     */
    List<CourseSchedule> selectTeacherCourse(Integer teacherId, Integer semester);

    /**
     * 查询教师所带课程的学生成绩
     * @param teacherId
     * @param semester
     * @param courseId
     * @return
     */
    List<Score> selectTeacherCourseScore(Integer teacherId, Integer semester, Integer courseId);
}
