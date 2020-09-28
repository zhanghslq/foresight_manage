package com.zhs.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.zhs.entity.DownBoxData;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.security.DenyAll;
import java.util.List;

/**
 * @author: zhs
 * @date: 2020/9/28 22:59
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownBoxNameVO {
    private Integer id;

    @ApiModelProperty("下拉框的名字")
    private String name;

    @ApiModelProperty(value = "下拉框类型的id")
    private Integer downBoxTypeId;

    private List<DownBoxData> downBoxDataList;
}
