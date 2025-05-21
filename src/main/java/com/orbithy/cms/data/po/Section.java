package com.orbithy.cms.data.po;

import com.baomidou.mybatisplus.annotation.*;
import com.orbithy.cms.data.enums.Major;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.EnumValue;

@Data
@NoArgsConstructor
@TableName("section")
public class Section {
    @TableId(type = IdType.AUTO)
    private Integer id; // 班级唯一ID

    private Major major; // 专业
    private Integer advisorId; // 导员ID（外键）
    private String grade;
    private String number; // 班级编号

}

