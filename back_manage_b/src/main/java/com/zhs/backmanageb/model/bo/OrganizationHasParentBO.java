package com.zhs.backmanageb.model.bo;

import lombok.Data;
import org.aspectj.lang.annotation.DeclareAnnotation;

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
