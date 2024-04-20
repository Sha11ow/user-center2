package com.shu.usercenter2.mapper;

import com.shu.usercenter2.domain.CourseSelection;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
* @author huxing
* @description 针对表【course_selection】的数据库操作Mapper
* @createDate 2024-04-14 23:09:51
* @Entity com.shu.usercenter2.domain.CourseSelection
*/
public interface CourseSelectionMapper extends BaseMapper<CourseSelection> {
    // 根据学生ID查询课程选择记录
    @Select("SELECT * FROM course_selection WHERE student_id = #{studentId}")
    List<CourseSelection> findCourseSelectionsByStudentId(Integer studentId);


}




