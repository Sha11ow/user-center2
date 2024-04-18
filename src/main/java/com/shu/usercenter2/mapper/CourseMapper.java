package com.shu.usercenter2.mapper;

import com.shu.usercenter2.domain.Course;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author huxing
* @description 针对表【course】的数据库操作Mapper
* @createDate 2024-04-14 23:09:51
* @Entity com.shu.usercenter2.domain.Course
*/
public interface CourseMapper extends BaseMapper<Course> {
    Course selectByCourseInfo(@Param("courseId") Integer courseId, @Param("courseName") String courseName);
}




