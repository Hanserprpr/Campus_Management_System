package com.orbithy.cms.data.dto;

import com.orbithy.cms.data.po.Status;
import com.orbithy.cms.data.po.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StudentCardDTO {
    private User user;
    private Status status;
}
