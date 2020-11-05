package com.zhs.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhs.entity.Resume;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhs.model.dto.ResumeDTO;
import com.zhs.model.vo.*;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
public interface ResumeService extends IService<Resume> {

    Page<ResumeVO> pageSelf(Resume resume, Page<Resume> resumePage, Date createTimeBegin, Date createTimeEnd);

    ResumeDTO dealWord(String filename, Long currentStatusId);

    List<InputStatisticsVO> expertInputStatistics();


    /**
     * 性别级别分布
     * @return
     */
    List<ResumeSexLevelVO> genderRate();

    /**
     * 年龄级别分布
     * @return
     */
    List<ResumeAgeLevelVO> ageLevelList();

    List<ResumeLevelAreaVO> listByArea(Long areaId, Long levelId);

    List<Resume> listByIdsSelf(List<Long> resumeIds);
}
