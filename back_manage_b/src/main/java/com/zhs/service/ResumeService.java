package com.zhs.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhs.entity.Resume;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhs.model.dto.ResumeDTO;
import com.zhs.model.vo.InputStatisticsVO;
import com.zhs.model.vo.ResumeVO;

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

    Page<ResumeVO> pageSelf(Resume resume, Page<Resume> resumePage);

    ResumeDTO dealWord(String filename, Long currentStatusId);

    List<InputStatisticsVO> expertInputStatistics();


}
