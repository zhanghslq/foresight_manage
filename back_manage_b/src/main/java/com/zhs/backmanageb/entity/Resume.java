package com.zhs.backmanageb.entity;

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

    @ApiModelProperty(value = "行政级别id")
    private Long levelId;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("出生日期 yyyy-MM-dd")
    private Date birthday;

    @ApiModelProperty("地区id")
    private Long areaId;

    @ApiModelProperty("地区id集合")
    private String areaIdArray;

    @ApiModelProperty("地区全名")
    private String areaName;

    @ApiModelProperty(value = "当前单位")
    private String company;

    @ApiModelProperty(value = "民族")
    private Long nation;

    @ApiModelProperty(value = "党派")
    private Long parties;

    @ApiModelProperty(value = "0女，1男")
    private Integer sex;

    @ApiModelProperty(value = "当前职务")
    private String job;

    @ApiModelProperty(value = "组织（政治身份）")
    private String organization;

    @ApiModelProperty(value = "职务(政治身份)")
    private String organizationJob;

    @ApiModelProperty(value = "照片")
    private String photoUrl;

    @ApiModelProperty(value = "现在状态的id")
    private Long currentStatusId;

    @ApiModelProperty(value = "现在状态")
    private String currentStatus;

    @ApiModelProperty(value = "管理员id")
    private Long adminId;

    @ApiModelProperty(value = "文件url标志")
    private String wordUrl;

    @JsonIgnore
    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @JsonIgnore
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
