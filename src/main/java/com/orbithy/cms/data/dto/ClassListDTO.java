package com.orbithy.cms.data.dto;

import com.orbithy.cms.data.po.Classes;
import lombok.Data;

@Data
public class ClassListDTO {//获取课程列表DTO
    private int id;
    private String name;
    private Integer point;
    private String term;
    private String classNum;
    private int peopleNum;
    private Classes.CourseStatus status;
    private String teacherName;
    private float regularRatio;
    private float finalRatio;
    private Classes.CourseType type;
    private String college;
}
