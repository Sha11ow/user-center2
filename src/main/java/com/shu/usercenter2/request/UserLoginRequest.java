package com.shu.usercenter2.request;

import lombok.Data;

@Data
public class UserLoginRequest {
    private static final long serialVersionUID = 2L;

    private int id;

    private String password;
}
