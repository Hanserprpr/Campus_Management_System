package com.orbithy.cms.data.dto;

import com.orbithy.cms.data.po.Section;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionDTO {
    private Integer id; // 班级唯一ID

    private Section.Major major; // 专业
    private Integer advisorId; // 导员ID（外键）
    private String grade;
    private String number; // 班级编号
    private String advisor;

}