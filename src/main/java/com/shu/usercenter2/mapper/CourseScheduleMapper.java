package com.shu.usercenter2.mapper;

import com.shu.usercenter2.domain.CourseSchedule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
* @author huxing
* @description 针对表【course_schedule】的数据库操作Mapper
* @createDate 2024-04-14 23:09:51
* @Entity com.shu.usercenter2.domain.CourseSchedule
*/
public interface CourseScheduleMapper extends BaseMapper<CourseSchedule> {

    /**
     * 根据条件查询课程安排
     *
     * @param courseId   课程ID
     * @param semester   学期
     * @param teacherId  教师ID
     * @param time       时间
     * @param capacity   容量
     * @return          符合条件的课程安排列表
     */
    List<CourseSchedule> selectCourseSchedule(
            @Param("courseId") Integer courseId,
            @Param("semester") Integer semester,
            @Param("teacherId") Integer teacherId,
            @Param("time") String time,
            @Param("capacity") Integer capacity);


    @Update("UPDATE course_schedule SET capacity = capacity - 1 WHERE  semester = #{semester} AND time = #{time} AND teacher_id = #{teacherId} AND course_id = #{courseId}")
    int updateCapacityByData(@Param("semester") Integer semester, @Param("time") String time, @Param("teacherId") Integer teacherId, @Param("courseId") Integer courseId);

}




