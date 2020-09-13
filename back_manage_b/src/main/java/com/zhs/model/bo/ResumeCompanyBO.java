package com.zhs.model.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: zhs
 * @date: 2020/8/17 11:20
 */
@Data
public class ResumeCompanyBO {
    private Long id;
    private String company;
    private String job;
}
