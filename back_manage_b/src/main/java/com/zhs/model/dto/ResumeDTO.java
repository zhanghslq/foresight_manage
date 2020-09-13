package com.zhs.model.dto;

import com.zhs.entity.ExperienceRecord;
import com.zhs.entity.Resume;
import com.zhs.entity.ResumeCompany;
import lombok.Data;

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


    private List<ResumeCompany> resumeCompanyList;

    /**
     * 政治身份的单位职务
     */
    private List<ResumeCompany> politicsResumeCompanyList;


}
