package com.zhs.model.dto;

import com.zhs.entity.Organization;
import com.zhs.model.bo.OrganizationTagBO;
import lombok.Data;

import java.util.List;

/**
 * @author: zhs
 * @date: 2020/8/9 14:34
 */
@Data
public class OrganizationDTO {
    private Organization organization;
    private List<OrganizationTagBO> tags;
}
