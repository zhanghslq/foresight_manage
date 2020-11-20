package com.zhs.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;

import java.time.LocalDate;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javafx.scene.chart.ValueAxis;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 联系记录表
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ConcatRecord对象", description="联系记录表")
public class ConcatRecord implements Serializable {

    private static final long serialVersionUID=1L;

    @ExcelProperty(value = "记录编号",index = 0)
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ExcelIgnore
    @ApiModelProperty(value = "联络人(主动联系别人)")
    private Long operatorId;

    @ExcelProperty(value = "联络人",index = 2)
    @ApiModelProperty(value = "联络人姓名")
    private String operatorName;

    @ExcelIgnore
    @ApiModelProperty(value = "被联系人")
    private Long concatPersonId;

    @ExcelProperty(value = "姓名",index = 3)
    @ApiModelProperty(value = "被联系人姓名")
    private String concatPersonName;

    @ApiModelProperty(value = "联系人类型,0联系人，1专家")
    private Integer personType;


    @ExcelIgnore
    @ApiModelProperty(value = "单位(关联组织）")
    private Long companyId;

    @ExcelProperty(value = "单位",index = 4)
    @ApiModelProperty(value = "单位名称（字符串）")
    private String companyName;

    @ExcelProperty(value = "职务",index = 5)
    @ApiModelProperty("职位")
    private String job;

    @ExcelProperty(value = "沟通类型",index = 6)
    @ApiModelProperty(value = "联系类型")
    private String concatType;

    @ExcelProperty(value = "沟通频率",index = 7)
    @ApiModelProperty(value = "联系次数")
    private Integer concatCount;

    @ExcelProperty(value = "联系日期",index = 8)
    @ApiModelProperty(value = "联系日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate contactDate;

    @ExcelProperty(value = "沟通简报",index = 9)
    @ApiModelProperty(value = "沟通简报")
    private String briefing;

    @ExcelIgnore
    @JsonIgnore
    @ApiModelProperty(value = "删除标志")
    @TableLogic
    private Integer deleted;

    @ExcelProperty(value = "发布时间",index = 1)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ExcelIgnore
    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
