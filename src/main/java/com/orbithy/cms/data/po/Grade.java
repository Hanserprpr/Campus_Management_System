package com.orbithy.cms.data.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@TableName("grade")
public class Grade {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String studentId; // 学生ID（外键）
    private Integer courseId; // 课程ID（外键）
    private Integer regular; // 平时分
    @TableField("final")
    private Integer final_score; // 期末分
    private Integer grade;
    private String term;
    private Byte rank;
}
