package com.zhs.backmanageb.model.bo;

import com.zhs.backmanageb.entity.Contacts;
import com.zhs.backmanageb.entity.Leader;
import com.zhs.backmanageb.entity.Organization;
import lombok.Data;

import java.util.List;

/**
 * @author: zhs
 * @date: 2020/7/25 17:57
 */
@Data
public class OrganizationBO {

    /**
     * 组织
     */
    private List<Organization> organizations;

    /**
     * 领导人
     */
    private List<Leader> leaders;

    /**
     * 联系人
     */
    private List<Contacts> contacts;
}
