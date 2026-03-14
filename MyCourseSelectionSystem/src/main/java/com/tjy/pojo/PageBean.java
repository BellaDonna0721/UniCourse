package com.tjy.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页查询封装类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageBean<T> {
    private Long totals;
    private List<T> rows;
}
