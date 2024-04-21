package com.shu.usercenter2.mapper;

import com.shu.usercenter2.domain.Score;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
* @author huxing
* @description 针对表【score】的数据库操作Mapper
* @createDate 2024-04-14 23:09:51
* @Entity com.shu.usercenter2.domain.Score
*/
public interface ScoreMapper extends BaseMapper<Score> {

    /**
     * 根据课程ID、学期和学生ID获取单个Score对象。
     *
     * @param courseId   课程ID
     * @param semester   学期
     * @param studentId  学生ID
     * @return 匹配的Score对象，如果没有找到则返回null
     */
    Score getScoreByConditions(@Param("courseId") Integer courseId,@Param("semester") Integer semester,@Param("studentId") Integer studentId);

    /**
     * 更新指定学生在特定课程和学期下的分数。
     *
     * @param newScore 待更新的分数
     * @param courseId 课程ID
     * @param semester 学期
     * @param studentId 学生ID
     * @return 更新是否成功（受影响行数 > 0）
     */
    int updateScore(
            @Param("newScore") Integer newScore,
            @Param("courseId") Integer courseId,
            @Param("semester") Integer semester,
            @Param("studentId") Integer studentId
    );
}




