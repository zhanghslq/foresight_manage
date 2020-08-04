package com.zhs.backmanageb.model.dto;

import com.zhs.backmanageb.entity.Role;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author: zhs
 * @date: 2020/8/1 20:54
 */
@Data
public class AdminVO {
    private Long id;
    private String username;
    private String realName;
    private String mobile;
    private Date createTime;
    private List<Role> roleList;
}
