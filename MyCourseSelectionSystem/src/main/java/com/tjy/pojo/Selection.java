package com.tjy.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Selection {
    private Long id;
    private Integer userId;
    private Integer courseId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
