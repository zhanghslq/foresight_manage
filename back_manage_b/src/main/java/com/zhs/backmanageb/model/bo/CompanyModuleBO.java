package com.zhs.backmanageb.model.bo;

import com.zhs.backmanageb.entity.Company;
import com.zhs.backmanageb.entity.Contacts;
import com.zhs.backmanageb.entity.Leader;
import com.zhs.backmanageb.entity.Organization;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: zhs
 * @date: 2020/7/25 17:57
 */
@Data
@ApiModel("组织模块")
public class CompanyModuleBO {
    /**
     * 模块类型
     */
    @ApiModelProperty("组织模块的类型，0领导人模块，1下属组织模块，2联系人模块，根据type，取对应属性的值")
    private Integer type;

    @ApiModelProperty("模块名称")
    private String moduleName;

    @ApiModelProperty("模块id")
    private Long moduleId;
    /**
     * 下属组织
     */
    private List<Company> companyChildren;

    /**
     * 领导人
     */
    private List<Leader> leaders;

    /**
     * 联系人
     */
    private List<Contacts> contacts;

}
