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
}