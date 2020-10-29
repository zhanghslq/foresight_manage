package com.zhs.model.vo;

import com.zhs.entity.Organization;
import com.zhs.model.bo.LeaderBO;
import com.zhs.model.bo.ModuleFrontBO;
import com.zhs.model.bo.ModuleSimple;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: zhs
 * @since: 2020/10/29 11:53
 */
@Data
public class OrganizationFrontVO {
    private Organization organization;
    @ApiModelProperty("正职务")
    private List<LeaderBO> justLeaderList;
    @ApiModelProperty("副职务")
    private List<LeaderBO> viceLeaderList;
    @ApiModelProperty("其他")
    private List<LeaderBO> otherLeaderList;

    @ApiModelProperty("下属机构的列表")
    private List<ModuleFrontBO> moduleList;
}

