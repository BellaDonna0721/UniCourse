package com.tjy.controller;

import com.tjy.pojo.Course;
import com.tjy.pojo.PageBean;
import com.tjy.pojo.Result;
import com.tjy.pojo.User;
import com.tjy.service.CourseService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@Slf4j
public class CourseController {

    @Autowired
    private CourseService courseService;

    /**
     * 添加新课程
     * @param course
     * @return
     */
    @PostMapping("/course")
    public Result add(@RequestBody Course course){
        log.info("创建新课程:{}", course);
        courseService.add(course);
        return Result.success(course);
    }

    /**
     * 修改课程
     * @param course
     * @return
     */
    @PutMapping("/course")
    public Result update(@RequestBody Course course){
        log.info("修改课程:{}", course);
        return courseService.update(course);
    }

    /**
     * 批量删除课程（也可以只删一个）
     */
    @DeleteMapping("/course/{ids}")
    public Result delete(@PathVariable List<Integer> ids){
        log.info("删除课程,ids:{}",ids);
        courseService.delete(ids);
        return Result.success();
    }

    /**
     * 条件查询
     * @param page  页数
     * @param pageSize  每页记录数
     * @param courseCode    课程码（模糊匹配）
     * @param courseName    课程名称（模糊匹配）
     * @param teacherId     授课教师id
     * @param minCredit     最小学分数（默认0）
     * @param maxCredit     最大学分数（默认10）
     * @param minCapacity   最小课程容量（默认0）
     * @param maxCapacity   最大课程容量（默认1000）
     * @param isRemaining   是否有余量（true忽略零余量）
     * @return
     */
    @GetMapping("/course")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize,
                       String courseCode,
                       String courseName,
                       Integer teacherId,
                       @RequestParam(defaultValue = "0") BigDecimal minCredit,
                       @RequestParam(defaultValue = "10") BigDecimal maxCredit,
                       @RequestParam(defaultValue = "0") Integer minCapacity,
                       @RequestParam(defaultValue = "1000")Integer maxCapacity,
                       Boolean isRemaining){
        log.info("条件查询课程,分页参数:" + page + " " +pageSize);
        PageBean<Course> pageBean = courseService.list(page, pageSize, courseCode, courseName, teacherId, minCredit, maxCredit, minCapacity, maxCapacity, isRemaining);
        return Result.success(pageBean);
    }

    /**
     * 根据id查询课程
     * @param id
     * @return
     */
    @GetMapping("/course/{id}")
    public Result getById(@PathVariable Integer id){
        log.info("根据id查询课程:" + id);
        //Course course = courseService.getById(id);
        return courseService.queryById(id);
    }
}
