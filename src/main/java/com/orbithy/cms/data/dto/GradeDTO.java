package com.orbithy.cms.data.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GradeDTO {
    private Integer studentId;
    private String username;
    private Integer regular; // 平时分
    private Integer finalScore; // 期末分
    private Integer grade;
    private String term;
    private Byte rank;
}
