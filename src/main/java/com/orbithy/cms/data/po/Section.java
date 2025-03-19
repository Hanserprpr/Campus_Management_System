package com.orbithy.cms.data.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.EnumValue;

@Getter
@Setter
@NoArgsConstructor
@TableName("section")
public class Section {
    @TableId(type = IdType.AUTO)
    private Integer id; // 班级唯一ID

    private Major major; // 专业
    private Integer advisorId; // 导员ID（外键）
    private String grade;
    private String number; // 班级编号

    // 专业枚举类型
    @Getter
    public enum Major {
        软件工程("0"),
        数字媒体技术("1"),
        大数据("2"),
        AI("3");

        @EnumValue // 让 MyBatis-Plus 识别该字段的数据库存储值
        private final String value;

        Major(String value) {
            this.value = value;
        }

    }
}

