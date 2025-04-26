package com.orbithy.cms.data.po;

import lombok.Data;

@Data
public class Term {
    private String term;
    private boolean isOpen; // 是否开放选课
}
