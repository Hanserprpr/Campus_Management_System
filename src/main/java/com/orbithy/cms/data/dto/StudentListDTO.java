package com.orbithy.cms.data.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.orbithy.cms.data.po.Status;
import com.orbithy.cms.data.po.StudentStatus;
import lombok.Data;

@Data
public class StudentListDTO {
    @TableField("SDUId")
    private String SDUId;           // 学号
    private String username;        // 姓名
    private String sex;             // 性别
    private String major;           // 专业
    private Integer grade;          // 年级
    private Integer section;        // 班级
    private StudentStatus status;      // 状态
}