package com.orbithy.cms.data.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName("status")
public class Status {
    @TableId(value = "id") //  id 作为主键，关联 user 表
    private Integer id;

    @TableField(value = "grade")
    private Integer grade;

    @TableField(value = "section")
    private Integer section;

    @EnumValue
    @TableField(value = "status")
    private StudentStatus status;

    @TableField(value = "admission")
    private Integer admission;
    @TableField(value = "graduation")
    private Integer graduation;
}
