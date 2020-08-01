package com.zhs.backmanageb.model.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author: zhs
 * @date: 2020/8/1 20:54
 */
@Data
public class AdminVO {
    private Long id;
    private String username;
    private String realName;
    private Date createTime;
}
