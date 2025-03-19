package com.orbithy.cms.data.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.EnumValue;
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
    private Integer point;
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

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private Set<String> timeSet;

    // 处理 MySQL SET 类型，转换为 Java Set<Integer>
    public Set<Integer> getTimeSet() {
        if (this.time == null || this.time.isEmpty()) {
            return Set.of();
        }
        return Set.of(this.time.split(","))
                .stream()
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
    }

    public void setTimeSet(Set<Integer> timeSet) {
        this.time = timeSet.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    // 课程类型枚举
    @Getter
    public enum CourseType {
        REQUIRED("必修"),
        LIMITED("限选"),
        ELECTIVE("任选");

        private final String description;

        CourseType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // 课程状态枚举
    @Getter
    public enum CourseStatus {
        PENDING(0, "待审批"),
        APPROVED(1, "已通过"),
        REJECTED(2, "已拒绝");

        private final int code;
        private final String description;

        CourseStatus(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static CourseStatus fromCode(int code) {
            for (CourseStatus status : values()) {
                if (status.code == code) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid status code: " + code);
        }
    }

    /**
     * 将时间集合转换为逗号分隔的字符串
     */
    public void convertTimeSetToString() {
        if (timeSet != null && !timeSet.isEmpty()) {
            this.time = String.join(",", timeSet);
        }
    }

    /**
     * 将逗号分隔的字符串转换为时间集合
     */
    public void convertStringToTimeSet() {
        if (time != null && !time.isEmpty()) {
            this.timeSet = new HashSet<>(Arrays.asList(time.split(",")));
        }
    }

    /**
     * 验证时间段是否合法
     */
    public boolean isValidTime() {
        if (timeSet == null || timeSet.isEmpty()) {
            return false;
        }

        // 时间段格式：星期-节次（如：1-1表示周一第1节）
        // 星期范围：1-7
        // 节次范围：1-12
        for (String timeSlot : timeSet) {
            String[] parts = timeSlot.split("-");
            if (parts.length != 2) {
                return false;
            }

            try {
                int day = Integer.parseInt(parts[0]);
                int period = Integer.parseInt(parts[1]);
                
                // 验证星期和节次的范围
                if (day < 1 || day > 7 || period < 1 || period > 12) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return true;
    }

    /**
     * 验证课程容量是否合法
     */
    public boolean isValidCapacity() {
        return capacity != null && capacity > 0;
    }

    /**
     * 验证学期格式是否合法
     */
    public boolean isValidTerm() {
        return term != null && term.matches("\\d{4}-\\d{4}-[12]");
    }

    /**
     * 验证周次是否合法
     */
    public boolean isValidWeeks() {
        return weekStart != null && weekEnd != null && 
               weekStart > 0 && weekEnd >= weekStart && 
               weekEnd <= 20; // 假设最多20周
    }
}
