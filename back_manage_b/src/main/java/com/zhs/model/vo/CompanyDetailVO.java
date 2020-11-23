package com.zhs.model.vo;

import com.zhs.model.bo.CompanyRelationshipBO;
import com.zhs.model.bo.ModuleSimple;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: zhs
 * @since: 2020/11/23 16:20
 */
@Data
public class CompanyDetailVO {
    private Long id;
    @ApiModelProperty("企业名称")
    private String name;
    @ApiModelProperty("行政级别")
    private String levelName;
    @ApiModelProperty("所属类别")
    private String typeName;

    @ApiModelProperty("根据上级关系类型分组")
    private List<CompanyRelationshipBO> companyRelationshipBOList;
    @ApiModelProperty("地址")
    private String address;
    @ApiModelProperty("网址")
    private String website;


}
