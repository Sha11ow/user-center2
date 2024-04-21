package com.shu.usercenter2.request;

import lombok.Data;

@Data
public class UserUpdateScoreRequest {
    private static final long serialVersionUID = 2L;
    private Integer course_id;
    private Integer student_id;
    private Integer new_score;
    private Integer semester;

}
