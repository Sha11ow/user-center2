package com.shu.usercenter2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shu.usercenter2.domain.*;
import com.shu.usercenter2.mapper.*;
import com.shu.usercenter2.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    @Autowired
    private CourseSelectionService courseSelectionService;
    @Autowired
    private CourseSelectionMapper courseSelectionMapper;
    @Autowired
    private ScoreService scoreService;
    @Autowired
    private ScoreMapper scoreMapper;

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
        Object o = request.getSession().getAttribute(USER_LOGIN_STATE);
        if(o!=null){
            log.info("用户信息已存入session");
        }

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
    @Override
    public boolean courseSelect(Integer courseId, Integer semester,
                                Integer teacherId, String time,
                                Integer capacity,
                                Integer studentId) {

        //除capacity的字段都不能为空，否则返回false
        if (courseId == null || semester == null || teacherId == null || StringUtils.isBlank(time)  || studentId == null) {
            log.info("参数不能为空");
            return false;
        }

        //检查课程是否存在
        List<CourseSchedule> courseSchedules = selectCourseSchedule(courseId, semester, teacherId, time, capacity);
        if (courseSchedules.isEmpty()) {
            log.info("课程表不存在");
            return false;
        }

        //这里因为需要所有字段，所以最多也就一个对应课程表
        CourseSchedule courseSchedule = courseSchedules.get(0);

        //检查课程是否已满
        if (courseSchedule.getCapacity() <= 0) {
            log.info("课程已满");
            return false;
        }

        //检查是否已选过该课程
        QueryWrapper<CourseSelection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        queryWrapper.eq("student_id", studentId);
        int count = courseSelectionService.count(queryWrapper);
        if (count > 0) {
            log.info("已选过该课程");
            return false;
        }

        //检查是否会有时间冲突
        List<CourseSelection> courseSelections = courseSelectionMapper.findCourseSelectionsByStudentId(studentId);

        if (!courseSelections.isEmpty()) {
            for (CourseSelection courseSelection : courseSelections) {
                List<CourseSchedule> schedules = selectCourseSchedule(courseSelection.getCourse_id(),
                        courseSelection.getSemester(), courseSelection.getTeacher_id(), null, null);
                if (schedules.isEmpty()) {
                    log.info("课程表不存在,数据库数据有问题");
                    return false;
                }
                for (CourseSchedule schedule : schedules) {
                    if (schedule.getTime().equals(courseSchedule.getTime()) &&
                            schedule.getSemester().equals(courseSchedule.getSemester())) {
                        log.info("时间冲突");
                        return false;
                    }
                }
            }
        }


        //选课
        CourseSelection courseSelection = new CourseSelection();
        courseSelection.setCourse_id(courseId);
        courseSelection.setSemester(semester);
        courseSelection.setTeacher_id(teacherId);
        courseSelection.setStudent_id(studentId);
        boolean save = courseSelectionService.save(courseSelection);
        if (!save) {
            log.info("选课失败");
            return false;
        }

        //更新课程表
        courseSchedule.setCapacity(courseSchedule.getCapacity() - 1);
        int rowsAffected = courseScheduleMapper.updateCapacityByData(courseSchedule.getSemester(),
                courseSchedule.getTime(), courseSchedule.getTeacher_id(), courseSchedule.getCourse_id());
        if (rowsAffected == 0) {
            log.info("更新课程表失败");
            return false;
        }

        //更新成绩表
        Score score = new Score();
        score.setCourse_id(courseId);
        score.setSemester(semester);
        score.setStudent_id(studentId);
        score.setScore(0);
        boolean save1 = scoreService.save(score);
        if (!save1) {
            log.info("更新成绩表失败");
            return false;
        }

        return true;
    }

    /**
     * 学生学期课表查询
     * @param semester
     * @param studentId
     * @return
     */
    @Override
    public List<CourseSchedule> semesterCourseSelection(Integer studentId, Integer semester) {
        if (studentId == null || semester == null) {
            log.info("参数不能为空");
            return Collections.emptyList();
        }
        List<CourseSelection> courseSelections = courseSelectionMapper.findCourseSelectionsByStudentId(studentId);

        if (courseSelections.isEmpty()) {
            log.info("未找到选课记录");
            return Collections.emptyList();
        }
        List<CourseSchedule> new_courseSchedules= new ArrayList<>();
        for (CourseSelection courseSelection : courseSelections) {
            if (!Objects.equals(courseSelection.getSemester(), semester)) {
                continue;
            }
            List<CourseSchedule> courseSchedules = selectCourseSchedule(courseSelection.getCourse_id(),
                    courseSelection.getSemester(), courseSelection.getTeacher_id(), null, null);
            if (courseSchedules.isEmpty()) {
                log.info("课程表不存在,数据库数据有问题");
                return Collections.emptyList();
            }
            CourseSchedule courseSchedule = courseSchedules.get(0);
            new_courseSchedules.add(courseSchedule);
        }
        return new_courseSchedules;
    }

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
    @Override
    public boolean courseWithdraw(Integer courseId, Integer semester, Integer teacherId, String time, Integer capacity, Integer studentId) {

        //除capacity的字段都不能为空，否则返回false
        if (courseId == null || semester == null || teacherId == null || StringUtils.isBlank(time)  || studentId == null) {
            log.info("参数不能为空");
            return false;
        }

        //检查课程是否存在
        List<CourseSchedule> courseSchedules = selectCourseSchedule(courseId, semester, teacherId, time, capacity);
        if (courseSchedules.isEmpty()) {
            log.info("课程表不存在");
            return false;
        }

        //这里因为需要所有字段，所以最多也就一个对应课程表
        CourseSchedule courseSchedule = courseSchedules.get(0);

        //删除选课记录
        QueryWrapper<CourseSelection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        queryWrapper.eq("student_id", studentId);
        boolean remove = courseSelectionService.remove(queryWrapper);
        if (!remove) {
            log.info("退课失败");
            return false;
        }

        //更新课程表
        courseSchedule.setCapacity(courseSchedule.getCapacity() + 1);
        int rowsAffected = courseScheduleMapper.updateCapacityByData(courseSchedule.getSemester(),
                courseSchedule.getTime(), courseSchedule.getTeacher_id(), courseSchedule.getCourse_id());
        if (rowsAffected == 0) {
            log.info("更新课程表失败");
            return false;
        }

        //删除成绩表
        QueryWrapper<Score> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("course_id", courseId);
        queryWrapper1.eq("student_id", studentId);
        boolean remove1 = scoreService.remove(queryWrapper1);
        if (!remove1) {
            log.info("删除成绩表失败");
            return false;
        }


        return true;
    }

    /**
     * 更新成绩
     * @param courseId
     * @param semester
     * @param studentId
     * @param newScore
     * @return
     */
    @Override
    public boolean updateScore(Integer courseId, Integer semester, Integer studentId, Integer newScore) {
        if (courseId==null||semester==null||studentId==null||newScore==null){
            log.info("有参数为空，失败");
            return false;
        }

        Score score = scoreMapper.getScoreByConditions(courseId, semester, studentId);

        if (score==null){
            log.info("未找到成绩记录");
            return false;
        }
        score.setScore(newScore);
        int cnt = scoreMapper.updateScore(newScore, courseId, semester, studentId);
        if (cnt==0){
            log.info("更新失败");
            return false;
        }
        return true;

    }


    /**
     * 添加成绩
     * @param courseId
     * @param semester
     * @param studentId
     * @param score1
     * @return
     */
    @Override
    public boolean addScore(Integer courseId, Integer semester, Integer studentId, Integer score1) {
        if (courseId==null||semester==null||studentId==null||score1==null){
            log.info("有参数为空，失败");
            return false;
        }

        Score score = scoreMapper.getScoreByConditions(courseId, semester, studentId);

        if (score!=null){
            log.info("已存在成绩记录");
            return false;
        }

        Score newScore = new Score();
        newScore.setCourse_id(courseId);
        newScore.setSemester(semester);
        newScore.setStudent_id(studentId);
        newScore.setScore(score1);

        return scoreService.save(newScore);
    }

    /**
     * 根据学生ID和学期查询成绩
     * @param studentId
     * @param semester
     * @return
     */
    @Override
    public List<Score> selectScore(Integer studentId, Integer semester) {
        if (studentId==null||semester==null){
            log.info("有参数为空，失败");
            return Collections.emptyList();
        }
        List<Score> scores = scoreMapper.selectScore(studentId, semester);
        if (!scores.isEmpty()){
            return scores;
        }
        log.info("未找到成绩记录");

        return Collections.emptyList();
    }

    /**
     * 根据学期,教师id查询成绩
     * @param teacherId
     * @return
     */
    @Override
    public List<CourseSchedule> selectTeacherCourse(Integer teacherId, Integer semester) {
        if (teacherId==null||semester==null){
            log.info("有参数为空，失败");
            return Collections.emptyList();
        }
        List<CourseSchedule> courseSchedules = courseScheduleMapper.selectTeacherCourse(teacherId, semester);
        if (!courseSchedules.isEmpty()){
            return courseSchedules;
        }
        log.info("未找到课程表记录");

        return Collections.emptyList();
    }

    /**
     * 根据学期,教师id,课程id查询成绩
     * @param teacherId
     * @return
     */
    @Override
    public List<Score> selectTeacherCourseScore(Integer teacherId, Integer semester, Integer courseId) {
        if (teacherId == null || semester == null || courseId == null) {
            log.info("有参数为空，失败");
            return Collections.emptyList();
        }

        // 先从选课表中查找课程
        List<CourseSelection> courseSchedules = courseSelectionMapper.selectTeacherCourseSelection(teacherId, semester, courseId);
        if (courseSchedules.isEmpty()) {
            log.info("没有人选课");
            return Collections.emptyList();
        }

        // 使用学生ID列表，尝试一次性查询所有学生的成绩（具体实现取决于ORM库和数据库类型）
        List<Integer> studentIds = courseSchedules.stream()
                .map(CourseSelection::getStudent_id)
                .collect(Collectors.toList());

        List<Score> scores = scoreMapper.selectScoresByStudentIdsAndSemesterAndCourseId(studentIds, semester, courseId);

        return scores;
    }

    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    @Override
    public User selectUserById(Integer id) {
        if (id==null){
            log.info("参数为空，失败");
            return null;
        }
        User user = this.getById(id);
        if (user!=null){
            return user;
        }
        log.info("未找到用户记录");
        return null;
    }



}




