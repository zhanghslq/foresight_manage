package com.zhs.model.vo;

import lombok.Data;

/**
 * @author: zhs
 * @since: 2020/11/30 15:54
 */
@Data
public class OrganizationSearchVO {
    private Long id;
    private String name;
    private Integer leaderCount;
    private Integer contactsCount;
    private String typeName;
}
