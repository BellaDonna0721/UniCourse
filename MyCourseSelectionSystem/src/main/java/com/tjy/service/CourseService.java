package com.tjy.service;

import com.tjy.pojo.Course;
import com.tjy.pojo.PageBean;
import com.tjy.pojo.Result;
import com.tjy.pojo.User;

import java.math.BigDecimal;
import java.util.List;

public interface CourseService {

    /**
     * 添加新课程
     * @param course
     */
    void add(Course course);

    /**
     * 修改课程
     * @param course
     */
    void updateById(Course course);

    /**
     * 根据id删除课程/批量删除课程
     * @param ids
     */
    void delete(List<Integer> ids);


    /**
     * 条件分页查询
     */
    PageBean<Course> list(Integer page, Integer pageSize, String courseCode,
                          String courseName, Integer teacherId, BigDecimal minCredit,
                          BigDecimal maxCredit, Integer minCapacity, Integer maxCapacity, Boolean isRemaining);

    /**
     * 根据id查询课程
     * @param id
     * @return
     */
    Course getById(Integer id);

    /**
     * 引入redis根据id查询数据
     * @param id
     * @return
     */
    Result queryById(Integer id);

    /**
     * 引入redis的更新逻辑
     * @param course
     * @return
     */
    Result update(Course course);
}
