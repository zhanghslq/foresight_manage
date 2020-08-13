package com.zhs.backmanageb.model.dto;

import com.zhs.backmanageb.entity.ExperienceRecord;
import com.zhs.backmanageb.entity.Resume;
import com.zhs.backmanageb.model.vo.ResumeVO;
import lombok.Data;
import org.aspectj.lang.annotation.DeclareAnnotation;

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
