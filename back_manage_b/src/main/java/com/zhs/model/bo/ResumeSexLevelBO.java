package com.zhs.model.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: zhs
 * @since: 2020/11/4 16:39
 */
@Data
public class ResumeSexLevelBO {
    @ApiModelProperty("性别")
    private Integer sex;
    @ApiModelProperty("级别")
    private Long levelId;
    @ApiModelProperty("数量")
    private Integer value;
}
