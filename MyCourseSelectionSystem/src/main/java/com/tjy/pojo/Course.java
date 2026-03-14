package com.tjy.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Course {
    private Integer id;
    private String courseCode;
    private String courseName;
    private String teacherId;
    private BigDecimal credit;
    private Integer capacity;
    private Integer selected;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
