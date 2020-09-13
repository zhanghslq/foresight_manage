package com.zhs.model.dto;

import lombok.Data;

/**
 * @author: zhs
 * @date: 2020/8/1 15:01
 */
@Data
public class AdminLoginReturnDTO {
    private Long id;
    private String username;
    private String realName;

    private String token;

}
