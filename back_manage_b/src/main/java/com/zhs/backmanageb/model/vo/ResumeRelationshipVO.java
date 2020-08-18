package com.zhs.backmanageb.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.zhs.backmanageb.entity.Resume;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: zhs
 * @date: 2020/8/18 8:53
 */
@Data
public class ResumeRelationshipVO {

    @ApiModelProperty(value = "编号")
    private Long id;

    @ApiModelProperty(value = "关系id")
    private Long relationshipId;

    @ApiModelProperty(value = "关系名称")
    private String relationship;

    private Resume resume;
}
