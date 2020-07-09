package com.zhs.backmanageb.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: zhs
 * @date: 2020/7/6 15:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
