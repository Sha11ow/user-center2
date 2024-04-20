package com.shu.usercenter2.request;

import lombok.Data;

@Data
public class UserSelectCourseScheduleRequest {
    private static final long serialVersionUID = 2L;
    private Integer course_id;
    private Integer semester;
    private Integer student_id;
    private Integer teacher_id;
    private String time;
    private Integer capacity;

}
