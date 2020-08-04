package com.zhs.backmanageb.model.vo;

import lombok.Data;

/**
 * 用户添加的数据
 * @author: zhs
 * @date: 2020/8/4 16:15
 */
@Data
public class AdminAddDataVO {
    /**
     * 添加的机构数量
     */
    private Integer organizationCount;
    /**
     * 添加的简历数量
     */
    private Integer resumeCount;
    /**
     * 导入数据数量，包括 专家、联系人、简历
     */
    private Integer importCount;
}
