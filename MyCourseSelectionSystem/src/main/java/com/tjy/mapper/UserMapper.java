package com.tjy.mapper;

import com.tjy.pojo.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {


    /**
     * 添加用户
     * @param user
     */
    @Insert("insert into user (username, name, role, major, create_time, update_time) " +
            "VALUES (#{username}, #{name}, #{role}, #{major}, #{createTime}, #{updateTime})")
    void add(User user);


    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    @Select("select * from user where id = #{id}")
    User getById(Integer id);


    /**
     * 更新用户信息
     * @param user
     */
    void update(User user);

    /**
     * 条件查询用户
     * @return
     */
    //@Select("select * from user")
    List<User> list(String username, String name, String role, String major);


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
    @Select("select * from user where username = #{username} and password = #{password}")
    User getByUserNameAndPassword(User user);
}
