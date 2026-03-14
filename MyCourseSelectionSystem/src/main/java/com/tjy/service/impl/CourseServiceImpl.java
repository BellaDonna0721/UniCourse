package com.tjy.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tjy.mapper.CourseMapper;
import com.tjy.mapper.SelectMapper;
import com.tjy.pojo.Course;
import com.tjy.pojo.PageBean;
import com.tjy.pojo.User;
import com.tjy.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    SelectMapper selectMapper;
    /**
     * 添加新课程
     * @param course
     */
    @Override
    public void add(Course course) {
        course.setCreateTime(LocalDateTime.now());
        course.setUpdateTime(LocalDateTime.now());
        course.setSelected(0);
        courseMapper.add(course);
    }

    /**
     * 修改课程
     * @param course
     */
    @Override
    public void update(Course course) {
        course.setUpdateTime(LocalDateTime.now());
        courseMapper.update(course);
    }

    /**
     * 根据id删除课程
     * @param ids
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<Integer> ids) {
        try {
            courseMapper.delete(ids);

            //同步删除选课记录
            for (Integer courseId : ids){
                selectMapper.deleteByCourseId(courseId);
            }
        } finally {

        }
    }

    /**
     * 条件分页查询
     */
    @Override
    public PageBean<Course> list(Integer page, Integer pageSize, String courseCode, String courseName,
                         Integer teacherId, BigDecimal minCredit, BigDecimal maxCredit,
                         Integer minCapacity, Integer maxCapacity, Boolean isRemaining) {
        PageHelper.startPage(page, pageSize);
        List<Course> courseList = courseMapper.list(courseCode, courseName, teacherId,
                minCredit, maxCredit, minCapacity, maxCapacity, isRemaining);
        // 使用 PageInfo 包装结果
        PageInfo<Course> pageInfo = new PageInfo<>(courseList);

        PageBean<Course> pageBean = new PageBean(
                pageInfo.getTotal(),  // 总记录数
                pageInfo.getList()     // 当前页数据
        );
        return pageBean;
    }

    /**
     * 根据id查询课程
     * @param id
     * @return
     */
    @Override
    public Course getById(Integer id) {
        Course course = courseMapper.getById(id);
        return course;
    }
}
