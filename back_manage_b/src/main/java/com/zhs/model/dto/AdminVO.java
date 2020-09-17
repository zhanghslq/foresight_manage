package com.zhs.model.dto;

import com.zhs.entity.Role;
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
    private Integer status;
    private Date createTime;
    private List<Role> roleList;
    private Integer loginCount;
    private Long onlineTime;
}
