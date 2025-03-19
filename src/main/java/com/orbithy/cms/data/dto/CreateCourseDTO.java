package com.orbithy.cms.data.dto;

import lombok.Data;

import java.util.Set;

@Data
public class CreateCourseDTO {
    private String name;           // 课程名称
    private String category;       // 课程类别
    private Integer point;         // 学分
    private String classroom;      // 上课教室
    private Integer weekStart;     // 起始周
    private Integer weekEnd;       // 结束周
    private Integer period;        // 课时
    private Set<String> time;      // 上课时间段
    private String college;        // 开课学院
    private String term;           // 开课学期
    private String type;           // 课程类型（必修/限选/任选）
    private Integer capacity;      // 课程容量
}
