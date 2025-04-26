package com.orbithy.cms.data.dto;

import com.orbithy.cms.data.po.Section;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SectionDTO {
    private Section section;
    private String advisor;

    public SectionDTO(Section section, String advisor) {
        this.section = section;
        this.advisor = advisor;
    }
}