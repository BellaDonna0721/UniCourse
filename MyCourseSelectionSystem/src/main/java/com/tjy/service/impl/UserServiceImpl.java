package com.tjy.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tjy.mapper.CourseMapper;
import com.tjy.mapper.SelectMapper;
import com.tjy.mapper.UserMapper;
import com.tjy.pojo.Course;
import com.tjy.pojo.PageBean;
import com.tjy.pojo.User;
import com.tjy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    SelectMapper selectMapper;

    @Autowired
    CourseMapper courseMapper;
    /**
     * 添加用户
     * @param user
     */
    @Override
    public void add(User user) {
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.add(user);
    }

    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    @Override
    public User getById(Integer id) {
        User user = userMapper.getById(id);
        return user;
    }

    /**
     * 修改用户信息
     * @param user
     */
    @Override
    public void update(User user) {
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
    }

    /**
     * 条件查询用户信息
     *
     * @return
     */
    @Override
    public PageBean<User> list(Integer page, Integer pageSize,String username,
                         String name, String role, String major) {
        PageHelper.startPage(page, pageSize);
        List<User> userList = userMapper.list(username, name, role, major);
        // 使用 PageInfo 包装结果
        PageInfo<User> pageInfo = new PageInfo<>(userList);

        PageBean<User> pageBean = new PageBean(
                pageInfo.getTotal(),  // 总记录数
                pageInfo.getList()     // 当前页数据
        );
        return pageBean;
    }

    /**
     * 批量删除用户
     * @param ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Integer> ids) {
        try {

            //同时删除选课记录
            for (Integer userId : ids) {
                User user = userMapper.getById(userId);
                //若删除的用户为学生 则删除其所有选课记录
                if (user.getRole().equals("student")) selectMapper.deleteByUserId(userId);
                //若删除的用户为老师 则同步删除对应课程及选课记录
                if (user.getRole().equals("teacher")){
                    List<Course> courseList = courseMapper.list(null, null,
                            userId, null,null,null,null,null);
                    List<Integer> courseIdList = new ArrayList<>();
                    for (Course course : courseList){
                        courseIdList.add(course.getId());
                        selectMapper.deleteByCourseId(course.getId());
                    }
                    courseMapper.delete(courseIdList);

                }
            }

            userMapper.delete(ids);
        } finally {

        }

    }

    /**
     * 用户登录
     * @param user
     * @return
     */
    @Override
    public User login(User user) {
        return userMapper.getByUserNameAndPassword(user);
    }
}
