package com.orbithy.cms.data.po;

import lombok.Data;

@Data
public class Term {
    private String term;
    private boolean open; // 是否开放选课
    private boolean current; // 是否当前学期
}
