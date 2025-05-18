package com.orbithy.cms.data.dto;

import lombok.Data;

@Data
public class VerifyRequestDTO {
    private String ticket;
    private String code;

}
