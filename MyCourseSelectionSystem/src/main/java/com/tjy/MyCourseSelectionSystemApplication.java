package com.tjy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class MyCourseSelectionSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyCourseSelectionSystemApplication.class, args);
    }

}
