package com.orbithy.cms.data.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@TableName("course_reg")
public class CourseSelection {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer studentId;
    private Integer courseId;
    private String classNum;

//    @TableField(fill = FieldFill.INSERT)
//    private LocalDateTime createTime;
//
//    @TableField(fill = FieldFill.INSERT_UPDATE)
//    private LocalDateTime updateTime;
} 