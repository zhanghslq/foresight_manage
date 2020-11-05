package com.zhs.model.vo;

import lombok.Data;
import org.aspectj.lang.annotation.DeclareAnnotation;

/**
 * @author: zhs
 * @since: 2020/11/5 16:31
 */
@Data
public class ResumeLevelAreaVO {
    private Long areaId;
    private String areaName;
    private Integer resumeCount;
}
