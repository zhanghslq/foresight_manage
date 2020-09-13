package com.zhs.model.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: zhs
 * @date: 2020/8/7 16:20
 */
@Data
public class OrganizationModuleSeqDTO {
    @ApiModelProperty("需要排序的id，联系人，领导人，下属机构等")
    private Long id;
    @ApiModelProperty("模块id")
    private Long moduleId;
    @ApiModelProperty("排序")
    private Integer seq;
}
