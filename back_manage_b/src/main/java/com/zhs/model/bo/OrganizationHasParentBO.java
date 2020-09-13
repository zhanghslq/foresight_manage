package com.zhs.model.bo;

import lombok.Data;

/**
 * @author: zhs
 * @date: 2020/8/14 11:53
 */
@Data
public class OrganizationHasParentBO {
    private Long id;
    private String name;
    private OrganizationHasParentBO parent;
}
