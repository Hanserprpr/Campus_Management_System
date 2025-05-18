package com.orbithy.cms.data.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.orbithy.cms.data.po.Classes;
import lombok.Data;

@Data
public class GradeTermDTO {
    private int id;
    private int courseId;
    private String className;
    private int regular;
    private int finalScore;
    private int grade;
    private Byte rank;
    private int classNum;
    private int point;
    private int teacherId;
    private Classes.CourseType type;
    @TableField(value = "username")
    private String teacher;
}
