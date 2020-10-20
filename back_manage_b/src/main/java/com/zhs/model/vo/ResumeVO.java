package com.zhs.model.vo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhs.entity.ResumeCompany;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author: zhs
 * @date: 2020/8/5 16:31
 */
@Data
public class ResumeVO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "姓名")
    private String realName;

    @ApiModelProperty("行政级别id数组")
    private String levelIdArray;

    @ApiModelProperty("行政级别id")
    private Long levelId;

    @ApiModelProperty("行政级别名称")
    private String levelName;

    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
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

    @ApiModelProperty(value = "民族名称")
    private String nationName;

    @ApiModelProperty(value = "党派")
    private Long parties;

    @ApiModelProperty(value = "党派名称")
    private String partiesName;

    @ApiModelProperty(value = "0女，1男")
    private Integer sex;

    @ApiModelProperty(value = "当前职务")
    private String job;

    @ApiModelProperty("简历对应的单位职务列表")
    private List<ResumeCompany> resumeCompanyList;

    @ApiModelProperty("简历对应的政治身份职务列表")
    private List<ResumeCompany> politicsResumeCompanyList;

    @ApiModelProperty(value = "照片")
    private String photoUrl;

    @ApiModelProperty(value = "现在状态")
    private String currentStatus;

    @ApiModelProperty(value = "管理员id")
    private Long adminId;

    @ApiModelProperty(value = "管理员名字")
    private String adminName;

    /**
     * 现任职时间
     */
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    @ApiModelProperty(value = "现任职时间,（现在工作开始时间）")
    private Date beginWorkingTime;

    /**
     * 在职时间
     */
    @ApiModelProperty(value = "在职时间，（现在这份工作开始到现在）")
    private String workingDays;

    @ApiModelProperty(value = "年龄")
    private Integer age;

    @ApiModelProperty(value = "是否确认，默认0待确认，1已确认")
    private Integer isConfirm;


    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @JsonIgnore
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
