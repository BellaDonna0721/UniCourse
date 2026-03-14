package com.tjy.controller;

import com.tjy.pojo.PageBean;
import com.tjy.pojo.Result;
import com.tjy.pojo.User;
import com.tjy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 添加用户
     * @param user
     */
    @PostMapping("/user")
    public Result add(@RequestBody User user){
        log.info("添加用户:" + user);
        userService.add(user);
        return Result.success(user);
    }

    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    @GetMapping("/user/{id}")
    public Result getById(@PathVariable Integer id){
        log.info("根据id查询员工:" + id);
        User user = userService.getById(id);
        return Result.success(user);
    }

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    @PutMapping("/user")
    public Result update(@RequestBody User user){
        log.info("修改用户:" + user + "信息");
        userService.update(user);
        return Result.success();
    }

    /**
     * 条件查询用户
     * @param page  页数
     * @param pageSize  每页记录数
     * @param username 用户名
     * @param name  姓名（支持模糊查询）
     * @param role  角色（teacher或student）
     * @param major 专业
     * @return
     */
    @GetMapping("/user")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize,
                        String username,
                        String name,
                        String role,
                        String major){
        log.info("条件查询用户,分页参数:" + page + " " +pageSize);
        PageBean<User> pageBean = userService.list(page, pageSize, username, name, role, major);
        return Result.success(pageBean);
    }

    /**
     * 批量删除用户（也可以只删一个）
     * @param ids
     * @return
     */
    @DeleteMapping("/user/{ids}")
    public Result delete(@PathVariable List<Integer> ids){
        log.info("批量删除，ids:{}", ids);
        userService.delete(ids);
        return Result.success();
    }
}
