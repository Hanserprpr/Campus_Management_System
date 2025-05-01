package com.orbithy.cms.data.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
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
    private Integer major;
    private Byte permission;
    private String nation;
    private String ethnic;
    @TableField("PoliticsStatus")
    private String PoliticsStatus;
    private String campus;
}
