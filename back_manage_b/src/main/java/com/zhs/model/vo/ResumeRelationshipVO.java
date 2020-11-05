package com.zhs.model.vo;

import com.zhs.entity.Resume;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


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

    private List<Resume> resumeList;
}
