package com.shu.usercenter2.request;

import lombok.Data;

@Data
public class UserSelectScoreRequest {
    private static final long serialVersionUID = 2L;
    private Integer semester;
    private Integer student_id;
}
