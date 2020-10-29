package com.zhs.model.vo;

import com.zhs.model.bo.ModuleSimple;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 机构右侧的总览信息
 * @author: zhs
 * @since: 2020/10/29 11:17
 */
@Data
public class OrganizationInformationVO {
    private Long id;
    @ApiModelProperty("机构名字")
    private String name;
    @ApiModelProperty("行政级别")
    private String levelName;
    @ApiModelProperty("体系")
    private String hierarchyName;
    @ApiModelProperty("系统")
    private String systemName;

    @ApiModelProperty("模块")
    private List<ModuleSimple> moduleList;

}
