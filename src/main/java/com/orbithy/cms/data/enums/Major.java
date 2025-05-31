package com.orbithy.cms.data.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Major {
    MAJOR_0(0, "软件工程"),
    MAJOR_1(1, "数字媒体技术"),
    MAJOR_2(2, "大数据"),
    MAJOR_3(3, "AI");

    @EnumValue
    private final int code;

    private final String label;

    Major(int code, String label) {
        this.code = code;
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static Major fromCode(Integer code) {
        for (Major m : values()) {
            if (m.code == code) return m;
        }
        throw new IllegalArgumentException("无效的专业编号：" + code);
    }
}