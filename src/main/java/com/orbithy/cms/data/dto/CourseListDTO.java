package com.orbithy.cms.data.dto;

import lombok.Data;

@Data
public class CourseListDTO {
    private String classNum;       // 课序号
    private String name;           // 课程名称
    private String term;           // 学期
    private Integer point;         // 学分
    private Integer studentCount;  // 学生人数
} 