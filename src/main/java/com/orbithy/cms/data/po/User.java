package com.orbithy.cms.data.po;

import com.baomidou.mybatisplus.annotation.*;

import com.orbithy.cms.data.enums.Major;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String sex;
    private String email;
    private String phone;
    private String password;
    @TableField("SDUId")
    private String SDUId;
    private Major major;
    private Byte permission;
    private String nation;
    private String ethnic;
    @TableField("PoliticsStatus")
    private String PoliticsStatus;
    private String college;
    @TableField("GPA_rank")
    private Integer GPA_rank;

}

