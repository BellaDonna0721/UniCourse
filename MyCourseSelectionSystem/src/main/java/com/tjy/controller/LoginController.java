package com.tjy.controller;

import com.tjy.pojo.Result;
import com.tjy.pojo.User;
import com.tjy.service.UserService;
import com.tjy.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class LoginController {

    @Autowired
    UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody User user){
        log.info("用户登录:{}", user);
        User u = userService.login(user);

        //登陆成功，生成并下发令牌
        if (u != null){
            String jwt = JwtUtils.geneJsonWebToken(user);
            return Result.success(jwt);
        }
        //登录失败，返回错误信息
        else return Result.error("用户名或密码错误");
    }
}
