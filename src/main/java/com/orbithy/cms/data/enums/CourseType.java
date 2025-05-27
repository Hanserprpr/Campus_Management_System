package com.orbithy.cms.data.enums;

import lombok.Getter;

@Getter
public enum CourseType {
    必修("必修"),
    限选("限选"),
    任选("任选");

    private final String label;

    CourseType(String label) {
        this.label = label;
    }

    public static CourseType fromLabel(String label) {
        for (CourseType type : values()) {
            if (type.label.equals(label)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知课程类型: " + label);
    }
}
