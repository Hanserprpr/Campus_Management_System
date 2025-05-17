package com.orbithy.cms.data.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("notice")
public class Notice {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField(value = "username")
    private String createName;

    private String title;

    private String content;

    private LocalDateTime publishTime;

    private Integer visibleScope;

    private Integer creatorId;

    private Integer isTop;

    private Integer status;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
