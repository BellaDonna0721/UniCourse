package com.tjy.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tjy.mapper.CourseMapper;
import com.tjy.mapper.SelectMapper;
import com.tjy.pojo.Course;
import com.tjy.pojo.PageBean;
import com.tjy.pojo.Result;
import com.tjy.pojo.User;
import com.tjy.service.CourseService;
import com.tjy.utils.CacheClient;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class CourseServiceImpl implements CourseService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CacheClient cacheClient;

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
        // 新增数据会影响分页索引缓存，建议清理
        java.util.Set<String> keys = stringRedisTemplate.keys("cache:course:list:*");
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
        }
    }

    /**
     * 修改课程
     * @param course
     */
    @Override
    public void updateById(Course course) {
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

            //同步删除选课记录并清理缓存
            for (Integer courseId : ids){
                selectMapper.deleteByCourseId(courseId);
                //清理单个课程缓存
                //stringRedisTemplate.delete("cache:course:" + courseId);
                cacheClient.delete("cache:course:" + courseId);
            }
            //由于新增、删除、修改都会影响分页结果，建议清理所有分页索引缓存（以 cache:course:list: 开头）
            java.util.Set<String> keys = stringRedisTemplate.keys("cache:course:list:*");
            if (keys != null && !keys.isEmpty()) {
                stringRedisTemplate.delete(keys);
            }
        } finally {

        }
    }

    /**
     * 条件分页查询（引入 Redis 缓存）
     */
    @Override
    public PageBean<Course> list(Integer page, Integer pageSize, String courseCode, String courseName,
                         Integer teacherId, BigDecimal minCredit, BigDecimal maxCredit,
                         Integer minCapacity, Integer maxCapacity, Boolean isRemaining) {
        // 1. 构建唯一的缓存 Key (包含所有查询条件和分页参数)
        String listKey = "cache:course:list:" + page + ":" + pageSize + ":" + courseCode + ":" + courseName +
                        ":" + teacherId + ":" + minCredit + ":" + maxCredit + ":" + minCapacity +
                        ":" + maxCapacity + ":" + isRemaining;

        // 2. 从 Redis 查询“索引缓存”（ID 列表和总数）
        String listJson = cacheClient.get(listKey);

        if (listJson != null && !listJson.isEmpty()) {
            // 缓存命中：解析出 ID 列表和总数
            JSONObject jsonObject = JSON.parseObject(listJson);
            Long total = jsonObject.getLong("total");
            List<Integer> ids = jsonObject.getJSONArray("ids").toJavaList(Integer.class);

            // 根据 ID 列表去 Redis 查询每一个课程的“实体缓存”
            List<Course> courses = new ArrayList<>();
            boolean allInCache = true;

            for (Integer id : ids) {
                String courseKey = "cache:course:" + id;
                String courseJson = cacheClient.get(courseKey);

                if (courseJson != null && !courseJson.isEmpty()) {
                    courses.add(JSON.parseObject(courseJson, Course.class));
                } else {
                    // 如果有一个课程缓存失效了，建议重新查库以保证数据完整性
                    allInCache = false;
                    break;
                }
            }

            if (allInCache) {
                return new PageBean<>(total, courses);
            }
        }

        // 3. 缓存未命中或实体缓存失效：查询数据库
        PageHelper.startPage(page, pageSize);
        List<Course> courseList = courseMapper.list(courseCode, courseName, teacherId,
                minCredit, maxCredit, minCapacity, maxCapacity, isRemaining);
        PageInfo<Course> pageInfo = new PageInfo<>(courseList);

        // 4. 写入缓存
        // 4.1 写入每一个课程的“实体缓存”（保证一个课程对应一条记录）
        List<Integer> ids = new ArrayList<>();
        for (Course course : courseList) {
            ids.add(course.getId());
            String courseKey = "cache:course:" + course.getId();
            //stringRedisTemplate.opsForValue().set(courseKey, JSONObject.toJSONString(course), 30L, TimeUnit.MINUTES);
            cacheClient.set(courseKey, course, 30L, TimeUnit.MINUTES);
        }

        // 4.2 写入“索引缓存”
        JSONObject listResult = new JSONObject();
        listResult.put("total", pageInfo.getTotal());
        listResult.put("ids", ids);
        // 分页索引缓存时间可以稍微设置短一点（例如 10 分钟），或者在增删时主动清理
        //stringRedisTemplate.opsForValue().set(listKey, listResult.toJSONString(), 10L, TimeUnit.MINUTES);
        cacheClient.set(listKey, listResult, 10L, TimeUnit.MINUTES);

        return new PageBean<>(pageInfo.getTotal(), courseList);
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

    /**
     * 引入redis根据id查询数据
     * @param id
     * @return
     */
    @Override
    public Result queryById(Integer id) {
        //Course course = queryWithPassThrough(id);

        //改用封装的cacheClient类
        Course course = cacheClient.queryWithPassThrough(
                "cache:course:", id, Course.class, id2 -> getById(id2),
                30L + (int)(Math.random()*5), TimeUnit.MINUTES);
        if (course == null){
            return Result.error("课程不存在");
        }
        else{
            return Result.success(course);
        }
    }

    public Course queryWithPassThrough(Integer id){
        String key = "cache:course:" + id;
        //从redis查询缓存
        String courseJson = stringRedisTemplate.opsForValue().get(key);

        //若为空数据
        if (Objects.equals(courseJson, "")){
            return null;
        }
        //判断是否存在
        if (courseJson != null){
            //存在，直接返回
            Course course = JSON.parseObject(courseJson, Course.class);
            return course;
        }
        //不存在，根据id查询数据库
        else {
            Course course = getById(id);
            //不存在，返回错误
            if (course == null){
                //将空值写入redis
                stringRedisTemplate.opsForValue().set(key, "", 2L, TimeUnit.MINUTES);
                return null;
            }
            //存在，写入redis并返回
            int randomNum = (int)(Math.random()*5);
            stringRedisTemplate.opsForValue().set(key, JSONObject.toJSONString(course), 30L + randomNum, TimeUnit.MINUTES);
            return course;
        }
    }

    /**
     * 引入redis的更新逻辑
     * @param course
     * @return
     */
    @Override
    @Transactional
    public Result update(Course course) {
        Integer id = course.getId();
        if (id == null){
            return Result.error("课程id不能为空！");
        }
        //更新数据库
        updateById(course);
        //删除单个课程实体缓存
        stringRedisTemplate.delete("cache:course:" + id);
        //修改数据可能会改变搜索条件的结果，所以也要清理分页索引缓存
        java.util.Set<String> keys = stringRedisTemplate.keys("cache:course:list:*");
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
        }
        return Result.success();
    }
}
