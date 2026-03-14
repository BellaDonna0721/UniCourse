package com.tjy.mapper;

import com.tjy.pojo.Course;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface CourseMapper {

    /**
     * 新增课程
     * @param course
     */
    @Insert("insert into course (course_code, course_name, teacher_id, credit, capacity,  create_time, update_time) " +
            "VALUES (#{courseCode}, #{courseName}, #{teacherId}, #{credit}, #{capacity}, #{createTime}, #{updateTime})")
    void add(Course course);

    /**
     * 更新课程
     * @param course
     */
    void update(Course course);


    /**
     * 根据id删除课程/批量删除课程
     * @param ids
     */
    void delete(List<Integer> ids);

    /**
     * 分页查询课程
     */
    List<Course> list(String courseCode, String courseName, Integer teacherId,
                      BigDecimal minCredit, BigDecimal maxCredit,
                      Integer minCapacity, Integer maxCapacity, Boolean isRemaining);

    /**
     * 根据id查询课程
     * @param id
     * @return
     */
    @Select("select * from course where id = #{id}")
    Course getById(Integer id);
}
