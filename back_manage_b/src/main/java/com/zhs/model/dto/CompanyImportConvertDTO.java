package com.zhs.model.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 企业导入
 * @author: zhs
 * @date: 2020/9/10 14:49
 */
@Data
public class CompanyImportConvertDTO {
    @ExcelProperty(value = "名字",index = 0)
    private String name;

    @ExcelProperty(value = "完善度",index = 1)
    private Integer perfectValue;
    @ExcelProperty(value = "级别",index = 2)
    private String levelName;

    @ExcelProperty(value = "企业类别",index = 3)
    private String companyTypeName;

    @ExcelProperty(value = "关系类型",index = 4)
    private String relationshipType;

    @ExcelProperty(value = "是否上市,1是，0否",index = 5)
    private Integer isMarket;

    @ExcelProperty(value = "上市情况",index = 6)
    private String marketTypeName;

    @ExcelProperty(value = "上市代码",index = 7)
    private String markedCode;
    /**
     * 这里需要维护id，和id数组？
     */
    @ExcelProperty(value = "地区名称",index = 8)
    private String areaName;
    @ExcelProperty(value = "详细地址",index = 9)
    private String addressDetail;
    @ExcelProperty(value = "排序",index = 10)
    private Integer seq;

    @ExcelProperty(value = "网址",index = 11)
    private String website;
}
