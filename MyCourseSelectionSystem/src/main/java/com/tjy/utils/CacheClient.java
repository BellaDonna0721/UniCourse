package com.tjy.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tjy.pojo.Course;
import com.tjy.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
@Slf4j
public class CacheClient {
    private StringRedisTemplate stringRedisTemplate;

    public CacheClient(StringRedisTemplate stringRedisTemplate)
    {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    public String get(String key) {
        return key == null ? null : stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 设置redis缓存
     * @param key   键
     * @param value 值(Object类型)
     * @param time  过期时间
     * @param unit  过期时间单位
     */
    public void set(String key, Object value, Long time, TimeUnit unit){
        stringRedisTemplate.opsForValue().set(key, JSONObject.toJSONString(value), time, unit);
    }

    /**
     * 删除缓存
     * @param key 键
     */
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }
    /**
     * 设置redis缓存，使用逻辑过期
     * @param key
     * @param value
     * @param time
     * @param unit
     */
    public void setWithLogicalExpire(String key, Object value, Long time, TimeUnit unit){
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));
        stringRedisTemplate.opsForValue().set(key, JSONObject.toJSONString(redisData));
    }

    /**
     * 封装解决缓存穿透
     * @param keyPrefix key前缀
     * @param id    key id
     * @param type  返回值类型
     * @param dbFallBack    查询数据库接口函数
     * @param time  过期时间
     * @param unit  过期时间单位
     * @return
     * @param <R>
     * @param <ID>
     */
    public <R,ID> R queryWithPassThrough(
            String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallBack, Long time, TimeUnit unit){
        String key = keyPrefix + id;
        //从redis查询缓存
        String json = stringRedisTemplate.opsForValue().get(key);

        //若为空数据
        if (Objects.equals(json, "")){
            return null;
        }
        //判断是否存在
        if (json != null){
            //存在，直接返回
            return JSON.parseObject(json, type);
        }
        //不存在，根据id查询数据库
        else {
            R r = dbFallBack.apply(id);
            //不存在，返回错误
            if (r == null){
                //将空值写入redis
                stringRedisTemplate.opsForValue().set(key, "", 2L, TimeUnit.MINUTES);
                return null;
            }
            //存在，写入redis并返回
            this.set(key, r, time, unit);
            return r;
        }
    }
}
