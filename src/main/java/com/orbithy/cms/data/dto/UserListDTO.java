package com.orbithy.cms.data.dto;

import com.orbithy.cms.data.po.User;
import lombok.Data;

import java.util.List;

@Data
public class UserListDTO {
    List<User> user;
    int page;
}
