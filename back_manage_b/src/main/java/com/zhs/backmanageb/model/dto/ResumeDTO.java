package com.zhs.backmanageb.model.dto;

import com.zhs.backmanageb.entity.ExperienceRecord;
import com.zhs.backmanageb.entity.Resume;
import lombok.Data;
import org.aspectj.lang.annotation.DeclareAnnotation;

import java.util.List;

/**
 * @author: zhs
 * @date: 2020/7/31 10:49
 */
@Data
public class ResumeDTO {
    /**
     * 简历
     */
    private Resume resume;
    /**
     * 工作经历
     */
    private List<ExperienceRecord> experienceRecordList;
}
