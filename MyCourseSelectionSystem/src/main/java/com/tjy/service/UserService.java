package com.tjy.service;

import com.tjy.pojo.PageBean;
import com.tjy.pojo.User;

import java.util.List;

public interface UserService {
    /**
     * 添加用户
     * @param user
     */
    void add(User user);

    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    User getById(Integer id);

    /**
     * 修改用户信息
     * @param user
     */
    void update(User user);

    /**
     * 查询所有用户信息
     * @return
     */
    PageBean<User> list(Integer page, Integer pageSize, String username,
                  String name, String role, String major);


    /**
     * 批量删除用户
     * @param ids
     */
    void delete(List<Integer> ids);

    /**
     * 用户登录
     * @param user
     * @return
     */
    User login(User user);
}
