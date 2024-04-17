package com.shu.usercenter2.service;

import com.shu.usercenter2.domain.Course;
import com.shu.usercenter2.domain.User;
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
    Course selectCourse(String courseName, Integer courseId);
}
