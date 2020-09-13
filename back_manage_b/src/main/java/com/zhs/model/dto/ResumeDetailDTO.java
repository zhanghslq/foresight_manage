package com.zhs.model.dto;

import com.zhs.entity.ExperienceRecord;
import com.zhs.model.vo.ResumeVO;
import lombok.Data;

import java.util.List;

/**
 * @author: zhs
 * @date: 2020/8/13 14:01
 */
@Data
public class ResumeDetailDTO {
    /**
     * 简历
     */
    private ResumeVO resumeVO;
    /**
     * 工作经历
     */
    private List<ExperienceRecord> experienceRecordList;
}
