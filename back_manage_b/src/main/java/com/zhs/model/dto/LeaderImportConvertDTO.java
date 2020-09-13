package com.zhs.model.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 领导人导入Excel转换
 * @author: zhs
 * @date: 2020/8/31 8:55
 */
@Data
public class LeaderImportConvertDTO {
    @ExcelProperty(value = "职位",index = 0)
    private String position;
    @ExcelProperty(value = "级别",index = 1)
    private String levelName;
    @ExcelProperty(value = "姓名",index = 2)
    private String realName;
    @ExcelProperty(value = "排序",index = 3)
    private Integer seq;
}
