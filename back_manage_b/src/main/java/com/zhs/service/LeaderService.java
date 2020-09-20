package com.zhs.service;

import com.zhs.entity.Leader;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 领导人 服务类
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
public interface LeaderService extends IService<Leader> {

    void listUpload(Long moduleId, MultipartFile file);
}