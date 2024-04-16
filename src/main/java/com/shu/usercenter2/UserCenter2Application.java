package com.shu.usercenter2;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.shu.usercenter2.mapper")
public class UserCenter2Application {

    public static void main(String[] args) {
        SpringApplication.run(UserCenter2Application.class, args);
    }

}
