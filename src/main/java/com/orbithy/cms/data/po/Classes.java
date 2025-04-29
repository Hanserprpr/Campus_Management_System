package com.orbithy.cms.data.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.EnumValue;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

@Getter
@Setter
@NoArgsConstructor
@TableName("classes")
public class Classes {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;
    private String category;
    private float point;
    private Integer teacherId;
    private String classroom;
    private Integer weekStart;
    private Integer weekEnd;
    private Integer period;
    private String time;
    private String college;
    private String term;
    private String classNum;
    private CourseType type;
    private Integer capacity;
    private CourseStatus status;
    private String intro;
    private Integer examination;
    private String f_reason;
    private Boolean published;
    private BigDecimal regularRatio;
    private BigDecimal finalRatio;




    // 课程类型枚举
    @Getter
    public enum CourseType {
        必修("必修"),
        限选("限选"),
        任选("任选");

        private final String description;

        CourseType(String description) {
            this.description = description;
        }

    }

    // 课程状态枚举
    @Getter
    public enum CourseStatus {
        待审批(0, "待审批"),
        已通过(1, "已通过"),
        已拒绝(2, "已拒绝");

        @EnumValue
        private final int code;
        private final String description;

        CourseStatus(int code, String description) {
            this.code = code;
            this.description = description;
        }

    }
}