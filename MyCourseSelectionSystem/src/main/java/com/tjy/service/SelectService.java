package com.tjy.service;

import com.tjy.pojo.Selection;

import java.util.List;

public interface SelectService {

    /**
     * 添加选课
     * @param userId
     * @param courseId
     * @return
     */
    Selection add(Integer userId, Integer courseId);

    /**
     * 删除选课
     * @param userId
     * @param courseId
     */
    void delete(Integer userId, Integer courseId);

    /**
     * 查询指定用户的所有选课记录
     * @param userId
     * @return
     */
    List<Selection> getByUserId(Integer userId);

    /**
     * 查找指定课程的所有选课记录
     * @param courseId
     * @return
     */
    List<Selection> getByCourseId(Integer courseId);
}
