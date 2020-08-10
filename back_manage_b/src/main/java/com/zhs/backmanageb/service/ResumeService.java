package com.zhs.backmanageb.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhs.backmanageb.entity.Resume;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhs.backmanageb.model.dto.ResumeDTO;
import com.zhs.backmanageb.model.vo.ResumeVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
public interface ResumeService extends IService<Resume> {

    Page<ResumeVO> pageSelf(Page<Resume> resumePage);

    ResumeDTO dealWord(String filename);
}
