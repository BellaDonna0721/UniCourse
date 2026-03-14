package com.tjy.controller;

import com.tjy.pojo.Result;
import com.tjy.pojo.Selection;
import com.tjy.service.SelectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class SelectController {

    @Autowired
    SelectService selectService;


    /**
     * 添加选课记录
     * @param userId 学生id
     * @param courseId  课程id
     * @return
     */
    @PostMapping("/selection")
    public Result add(@RequestParam Integer userId, @RequestParam Integer courseId){
        log.info("学生id: " + userId + "选课id:" + courseId);
        Selection selection = selectService.add(userId, courseId);
        return Result.success(selection);
    }

    /**
     * 删除选课记录（退课）
     * @param userId
     * @param courseId
     * @return
     */
    @DeleteMapping("/selection/delete")
    public Result delete(@RequestParam Integer userId, @RequestParam Integer courseId){
        log.info("学生id: " + userId + "退课id:" + courseId);
        selectService.delete(userId, courseId);
        return Result.success();
    }

    /**
     * 根据用户id查找用户的所有选课记录
     * @param userId
     * @return
     */
    @GetMapping("/selection/user/{userId}")
    public Result getByUserId(@PathVariable Integer userId){
        log.info("查找id为{}用户的所有选课记录", userId);
        List<Selection> selections = selectService.getByUserId(userId);
        return Result.success(selections);
    }

    /**
     * 根据课程id查找指定课程的所有选课记录
     * @param courseId
     * @return
     */
    @GetMapping("/selection/course/{courseId}")
    public Result getByCourseId(@PathVariable Integer courseId){
        log.info("查找id为{}课程的所有选课记录", courseId);
        List<Selection> selections = selectService.getByCourseId(courseId);
        return Result.success(selections);
    }
}
