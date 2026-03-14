package com.tjy.service.impl;

import com.tjy.mapper.CourseMapper;
import com.tjy.mapper.SelectMapper;
import com.tjy.mapper.UserMapper;
import com.tjy.pojo.Course;
import com.tjy.pojo.Selection;
import com.tjy.pojo.User;
import com.tjy.service.SelectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SelectServiceImpl implements SelectService {
    @Autowired
    SelectMapper selectMapper;

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    UserMapper userMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Selection add(Integer userId, Integer courseId) {

        try {
            //检查用户是否存在
            User user = userMapper.getById(userId);
            if (user == null){
                throw new RuntimeException("用户不存在");
            }
            // 检查课程是否存在且容量未满
            Course course = courseMapper.getById(courseId);
            if (course == null) {
                throw new RuntimeException("课程不存在");
            }
            if (course.getSelected() >= course.getCapacity()) {
                throw new RuntimeException("课程容量已满");
            }

            // 检查用户是否已经选过该课程
            Selection existingSelection = selectMapper.findByUserIdAndCourseId(userId, courseId);
            if (existingSelection != null) {
                throw new RuntimeException("您已选择该课程，不可重复选择");
            }

            // 所有检查通过，执行选课操作
            Selection newSelection = new Selection();
            newSelection.setUserId(userId);
            newSelection.setCourseId(courseId);
            newSelection.setCreateTime(LocalDateTime.now());
            newSelection.setUpdateTime(LocalDateTime.now());

            selectMapper.add(newSelection);
            //选课成功后，更新课程的已选人数
            course.setSelected(course.getSelected() + 1); // 已选人数+1
            courseMapper.update(course);

            // 5. 返回新创建的选课记录
            return newSelection;
        } finally {

        }
    }

    /**
     * 删除选课记录
     * @param userId
     * @param courseId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Integer userId, Integer courseId) {
        try {
            //检查选课记录是否存在
            Selection selection = selectMapper.findByUserIdAndCourseId(userId, courseId);
            if (selection == null){
                throw new RuntimeException("未找到该选课记录");
            }
            Course course = courseMapper.getById(courseId);

            selectMapper.deleteById(selection.getId());
            //退课成功后，更新课程的已选人数
            course.setSelected(course.getSelected() - 1); // 已选人数-1
            courseMapper.update(course);
        } finally {

        }
    }

    /**
     * 根据用户id查找用户的所有选课记录
     * @param userId
     * @return
     */
    @Override
    public List<Selection> getByUserId(Integer userId) {
        List<Selection> selections = selectMapper.getByUserId(userId);
        return selections;
    }

    /**
     * 根据课程id查找指定课程的所有选课记录
     * @param courseId
     * @return
     */
    @Override
    public List<Selection> getByCourseId(Integer courseId) {
        List<Selection> selections = selectMapper.getByCourseId(courseId);
        return selections;
    }
}
