package com.orbithy.cms.data.po;

import com.baomidou.mybatisplus.annotation.*;
import com.orbithy.cms.data.enums.handler.CourseTypeHandler;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.orbithy.cms.data.enums.CourseType;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@TableName("classes")
public class Classes {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;
    private String category;
    private Float point;
    private Integer teacherId;
    @TableField(value = "classroom_id")
    private String classroomId;
    private Integer weekStart;
    private Integer weekEnd;
    private Integer period;
    private String time;
    private String college;
    private String term;
    private String classNum;
    @TableField(typeHandler = CourseTypeHandler.class)
    private CourseType type;
    private Integer capacity;
    private CourseStatus status;
    private String intro;
    private Integer examination;
    private String f_reason;
    private Boolean published;
    private BigDecimal regularRatio;
    private BigDecimal finalRatio;



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