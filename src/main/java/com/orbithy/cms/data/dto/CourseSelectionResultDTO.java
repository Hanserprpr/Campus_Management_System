package com.orbithy.cms.data.dto;

import lombok.Data;

@Data
public class CourseSelectionResultDTO {
    private Integer id;        // 课程ID
    private String classNum;         // 课序号
    private String name;             // 课程名称
    private float point;           // 学分
    private String type;             // 课程属性
    private String teacherName;      // 教师名称
    private String time;             // 上课时间
    private String classroom;        // 上课地点
    private Integer selectedCount;   // 已选人数
    private Integer capacity;        // 课程容量
    private String category;         // 课程类别
    private Integer weekStart;
    private Integer weekEnd;
}