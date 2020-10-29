package com.zhs.model.bo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: zhs
 * @since: 2020/10/29 12:46
 */
@Data
public class OrganizationSimple {

    @ApiModelProperty("组织id")
    private Long organizationId;
    @ApiModelProperty("组织名称")
    private String name;
    @ApiModelProperty("领导数量")
    private Integer leaderCount;
    @ApiModelProperty("联系人数量")
    private Integer contactsCount;
}
