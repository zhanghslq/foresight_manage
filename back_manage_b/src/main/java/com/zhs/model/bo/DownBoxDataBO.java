package com.zhs.model.bo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: zhs
 * @since: 2020/9/30 14:24
 */
@Data
public class DownBoxDataBO {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "类型,即创建的下拉框名字的id")
    private Integer type;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("排序")
    private Integer seq;

    private List<DownBoxDataBO> children;
}
