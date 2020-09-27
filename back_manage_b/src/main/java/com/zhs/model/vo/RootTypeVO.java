package com.zhs.model.vo;

import com.zhs.entity.CommonData;
import com.zhs.entity.OrganizationType;
import com.zhs.model.bo.OrganizationTypeBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author: zhs
 * @since: 2020/9/27 11:31
 */
@Data
@ApiModel("组织类别（为了树形控件，内部结构不一样）")
@Accessors(chain = true)
public class RootTypeVO {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("名字")
    private String name;
    private List<OrganizationTypeBO> children;
}
