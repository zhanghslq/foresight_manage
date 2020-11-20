package com.zhs.model.bo;

import lombok.Data;

import java.util.Date;

/**
 * @author: zhs
 * @since: 2020/10/29 12:34
 */
@Data
public class LeaderBO {
    private Long id;
    private String name;
    private Date startTime;
    private Long resumeId;
}
