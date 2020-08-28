package com.zhs.backmanageb.service.impl;

import com.zhs.backmanageb.entity.Leader;
import com.zhs.backmanageb.mapper.LeaderMapper;
import com.zhs.backmanageb.service.LeaderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 领导人 服务实现类
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Service
public class LeaderServiceImpl extends ServiceImpl<LeaderMapper, Leader> implements LeaderService {

    @Override
    public void listUpload(Long moduleId, MultipartFile file) {

    }
}
