package com.zhs.backmanageb.service;

import com.zhs.backmanageb.entity.OrganizationModule;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhs.backmanageb.model.dto.OrganizationModuleSeqDTO;

import java.util.List;

/**
 * <p>
 * 组织模块 服务类
 * </p>
 *
 * @author zhs
 * @since 2020-07-27
 */
public interface OrganizationModuleService extends IService<OrganizationModule> {

    void deleteDataAboutThis(Long id);

    void updateSeq(Long moduleId, List<OrganizationModuleSeqDTO> organizationModuleSeqDTOList);

    void copy(Long sourceModuleId, Long targetModuleId);

    void randomCopy(Long moduleId);
}
