package com.tjy.mapper;

import com.tjy.pojo.Selection;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SelectMapper {

    /**
     * 添加选课
     * @param newSelection
     */
    @Insert("insert into selection (user_id, course_id, create_time, update_time) VALUES (#{userId}, #{courseId}, #{createTime}, #{updateTime} )")
    void add(Selection newSelection);

    /**
     * 根据用户id和课程id查找记录（唯一）
     * @param userId
     * @param courseId
     * @return
     */
    @Select("select * from selection where user_id = #{userId} and course_id = #{courseId}")
    Selection findByUserIdAndCourseId(Integer userId, Integer courseId);

    /**
     * 根据选课记录id删除记录
     * @param id
     */
    @Delete("delete from selection where id = #{id}")
    void deleteById(Integer id);


    /**
     * 查询指定用户的所有选课记录
     * @param userId
     * @return
     */
    @Select("select * from selection where user_id = #{userId} order by update_time desc")
    List<Selection> getByUserId(Integer userId);

    /**
     * 查询指定课程的所有选课记录
     * @param courseId
     * @return
     */
    @Select("select * from selection where course_id = #{courseId} order by update_time desc")
    List<Selection> getByCourseId(Integer courseId);


    /**
     * 删除指定id用户的所有选课记录
     * @param userId
     */
    @Delete("delete from selection where user_id = #{userId}")
    void deleteByUserId(Integer userId);

    /**
     * 删除指定id课程的所有选课记录
     * @param courseId
     */
    @Delete("delete from selection where course_id = #{courseId}")
    void deleteByCourseId(Integer courseId);
}
