package com.orbithy.cms.data.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@TableName("Class_Course")
public class ClassCourse {
    private Integer id;
    private Integer classId;
    private Integer courseId;
    private Boolean isRequired;
}
