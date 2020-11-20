package com.zhs.service;

import com.zhs.entity.Company;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhs.model.bo.OrganizationHasParentBO;
import com.zhs.model.bo.OrganizationTagBO;
import com.zhs.model.vo.CompanyVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 企业 服务类
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
public interface CompanyService extends IService<Company> {

    CompanyVO queryByOrganizationType(Long organizationTypeId, Long areaId);

    CompanyVO queryByParentId(Long id);

    void dealTags(Long id, List<OrganizationTagBO> tags);

    OrganizationHasParentBO listParentById(Long organizationId);


    void listUpload(Long moduleId, MultipartFile file);

    List<Company> listByOrganizationType(Long organizationTypeId, Long areaId);

    List<Company> listByType(Long typeId);
}
