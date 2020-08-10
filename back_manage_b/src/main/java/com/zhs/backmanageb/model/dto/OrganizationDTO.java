package com.zhs.backmanageb.model.dto;

import com.zhs.backmanageb.entity.Organization;
import com.zhs.backmanageb.model.bo.OrganizationTagBO;
import lombok.Data;

import java.util.List;

/**
 * @author: zhs
 * @date: 2020/8/9 14:34
 */
@Data
public class OrganizationDTO {
    private Organization organization;
    List<OrganizationTagBO> tags;
}
