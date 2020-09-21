package com.zhs.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: zhs
 * @date: 2020/7/6 15:28
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "管理员")
public class Admin extends BaseEntity{

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @ApiModelProperty("用户姓名")
    private String username;
    @ApiModelProperty("密码")
    private String password;
    @ApiModelProperty("真实姓名")
    private String realName;
    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("管理员类型")
    private Integer type;
    @ApiModelProperty("用户状态，0正常，1冻结")
    private Integer status;

    @ApiModelProperty("登陆次数")
    private Integer loginCount;

    @ApiModelProperty("在线时长（单位 秒）")
    private Long onlineTime;


    @ApiModelProperty("操作次数（增删改）")
    private Integer operatorCount;


    @JsonIgnore
    private String salt;

    @JsonIgnore
    @ApiModelProperty(value = "删除标志")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty("创建时间")
    private Date createTime;
    @ApiModelProperty("更新时间")
    private Date updateTime;

}
