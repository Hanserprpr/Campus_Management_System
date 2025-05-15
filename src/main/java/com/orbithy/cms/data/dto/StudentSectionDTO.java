package com.orbithy.cms.data.dto;

import com.orbithy.cms.data.po.Section;
import lombok.Data;

@Data
public class StudentSectionDTO {
    private Integer id;
    private String username;
    private Integer sectionNumber;
    private Section.Major major;
    private String SDUId;
    private Integer regular; // 平时分
    private Integer finalScore; // 期末分
    private Integer grade;
    private String number;
}