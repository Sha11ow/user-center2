package com.shu.usercenter2.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName course_selection
 */
@TableName(value ="course_selection")
@Data
public class CourseSelection implements Serializable {
    /**
     * 01-春，02-夏，03-秋，04-冬
     */
    @TableId
    private Integer semester;

    /**
     * 
     */
    @TableId
    private Integer course_id;

    /**
     * 
     */
    @TableId
    private Integer teacher_id;

    /**
     * 
     */
    @TableId
    private Integer student_id;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        CourseSelection other = (CourseSelection) that;
        return (this.getSemester() == null ? other.getSemester() == null : this.getSemester().equals(other.getSemester()))
            && (this.getCourse_id() == null ? other.getCourse_id() == null : this.getCourse_id().equals(other.getCourse_id()))
            && (this.getTeacher_id() == null ? other.getTeacher_id() == null : this.getTeacher_id().equals(other.getTeacher_id()))
            && (this.getStudent_id() == null ? other.getStudent_id() == null : this.getStudent_id().equals(other.getStudent_id()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getSemester() == null) ? 0 : getSemester().hashCode());
        result = prime * result + ((getCourse_id() == null) ? 0 : getCourse_id().hashCode());
        result = prime * result + ((getTeacher_id() == null) ? 0 : getTeacher_id().hashCode());
        result = prime * result + ((getStudent_id() == null) ? 0 : getStudent_id().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", semester=").append(semester);
        sb.append(", course_id=").append(course_id);
        sb.append(", teacher_id=").append(teacher_id);
        sb.append(", student_id=").append(student_id);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}