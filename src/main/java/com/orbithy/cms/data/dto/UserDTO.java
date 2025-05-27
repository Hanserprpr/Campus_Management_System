package com.orbithy.cms.data.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.orbithy.cms.data.enums.Major;
import com.orbithy.cms.data.po.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTO {
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
    private Integer processed;

    public UserDTO(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.phone = user.getPhone();

        this.SDUId = user.getSDUId();
        this.major = user.getMajor();
        this.permission = user.getPermission();
        this.nation = user.getNation();
        this.college = user.getCollege();
        this.id = user.getId();
        this.sex = user.getSex();
        this.password = user.getPassword();
        this.ethnic = user.getEthnic();
        this.PoliticsStatus = user.getPoliticsStatus();
    }
}
