package com.zhs.model.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 下属机构导入，转换
 * @author: zhs
 * @date: 2020/8/31 8:59
 */
@Data
public class OrganizationImportConvertDTO {
    @ExcelProperty(value = "名字",index = 0)
    private String name;
    @ExcelProperty(value = "其他名字",index = 1)
    private String otherName;
    @ExcelProperty(value = "重要性",index = 2)
    private String importance;
    @ExcelProperty(value = "完善度",index = 3)
    private Integer perfectValue;
    @ExcelProperty(value = "级别",index = 4)
    private Integer isGovernment;
    @ExcelProperty(value = "体系",index = 5)
    private String hierarchy;
    @ExcelProperty(value = "系统",index = 6)
    private String levelName;
    @ExcelProperty(value = "下属单位,1是，0否",index = 7)
    private String system;
    @ExcelProperty(value = "机构类型",index = 8)
    private String commonType;
    /**
     * 这里需要维护id，和id数组？
     */
    @ExcelProperty(value = "协会学会",index = 9)
    private Integer isAssociation;
    @ExcelProperty(value = "是否脱钩",index = 11)
    private String isDecoupling;
    @ExcelProperty(value = "地区名称",index = 12)
    private String areaName;
    @ExcelProperty(value = "详细地址",index = 13)
    private String addressDetail;
    @ExcelProperty(value = "排序",index = 14)
    private Integer seq;

    @ExcelProperty(value = "网址",index = 15)
    private String website;


}
