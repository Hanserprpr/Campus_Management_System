package com.orbithy.cms.data.dto;

import com.orbithy.cms.data.po.Classes;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClassDTO {//详细信息DTO
    private Integer id;

    private String name;
    private String category;
    private Integer point;
    private Integer teacherId;
    private String teacherName;
    private String classroom;
    private Integer weekStart;
    private Integer weekEnd;
    private Integer period;
    private String time;
    private String college;
    private String term;
    private String classNum;
    private Classes.CourseType type;
    private Integer capacity;
    private Classes.CourseStatus status;
    private String intro;
    private Integer examination;
    private String f_reason;
    private Boolean published;
    private BigDecimal regularRatio;
    private BigDecimal finalRatio;

}
