package com.zhs.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author: zhs
 * @since: 2020/9/30 16:37
 */
@Data
public class DownBoxNameDetailVO {
    private Integer id;
    private String name;
    private Integer downBoxTypeId;
    private List<Integer> scopeIdList;
}
