package com.orbithy.cms.data.dto;

import com.orbithy.cms.data.po.Classes;
import lombok.Data;

@Data
public class ClassListDTO {
    private int id;
    private String name;
    private Integer point;
    private String term;
    private String classNum;
    private int peopleNum;
    private Classes.CourseStatus status;
}
