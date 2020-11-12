package com.zhs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Resume对象", description="")
public class Resume implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "姓名")
    private String realName;

    @ApiModelProperty("行政级别id数组")
    private String levelIdArray;

    @ApiModelProperty(value = "行政级别id")
    private Long levelId;

    @ApiModelProperty(value = "级别名称")
    private String levelName;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("出生日期 yyyy-MM-dd")
    private Date birthday;

    @ApiModelProperty(value = "出生日期")
    private String birthdayString;

    @ApiModelProperty("地区id")
    private Long areaId;

    @ApiModelProperty("地区id集合")
    private String areaIdArray;

    @ApiModelProperty("地区全名")
    private String areaName;

    @ApiModelProperty(value = "当前单位")
    private String company;

    @ApiModelProperty("民族id数组")
    private String nationIdArray;

    @ApiModelProperty(value = "民族")
    private Long nation;

    @ApiModelProperty("民族字符串")
    private String nationName;

    @ApiModelProperty("党派id数组")
    private String partiesArray;

    @ApiModelProperty(value = "党派")
    private Long parties;

    @ApiModelProperty(value = "党派名称")
    private String partiesName;

    @ApiModelProperty(value = "0女，1男")
    private Integer sex;

    @ApiModelProperty(value = "性别名称")
    private String sexName;

    @ApiModelProperty(value = "当前职务")
    private String job;

    @ApiModelProperty(value = "组织（政治身份）")
    private String organization;

    @ApiModelProperty(value = "职务(政治身份)")
    private String organizationJob;

    @ApiModelProperty(value = "照片")
    private String photoUrl;

    @ApiModelProperty("状态id数组")
    private String currentStatusIdArray;

    @ApiModelProperty(value = "现在状态的id")
    private Long currentStatusId;

    @ApiModelProperty(value = "现在状态")
    private String currentStatus;

    @ApiModelProperty(value = "管理员id")
    private Long adminId;

    @ApiModelProperty(value = "文件url标志")
    private String wordUrl;

    @ApiModelProperty(value = "word解析出来的内容")
    private String wordContent;

    @ApiModelProperty(value = "是否确认，默认0待确认，1已确认")
    private Integer isConfirm;

    @JsonIgnore
    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @JsonIgnore
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty("年龄，指定需要才会有，一般不存在")
    @TableField(exist = false)
    private Integer age;

    @ApiModelProperty(value = "简历来源，0普通添加，1直接分析入库")
    private Integer source;


}
