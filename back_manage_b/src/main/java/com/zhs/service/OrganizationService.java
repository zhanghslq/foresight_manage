package com.zhs.service;

import com.zhs.entity.Organization;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhs.model.bo.OrganizationHasParentBO;
import com.zhs.model.bo.OrganizationTagBO;
import com.zhs.model.vo.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 组织机构 服务类
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
public interface OrganizationService extends IService<Organization> {

    OrganizationVO queryByOrganizationType(Long organizationTypeId, Long areaId);

    OrganizationVO queryByParentId(Long id);

    void dealTags(Long id, List<OrganizationTagBO> tags);

    OrganizationHasParentBO listParentById(Long organizationId);

    void listUpload(Long moduleId, MultipartFile file);

    OrganizationInformationVO queryInformationById(Long id);

    OrganizationFrontVO queryFrontByOrganizationType(Long organizationTypeId, Long areaId);

    OrganizationFrontVO queryFrontByParentId(Long id);

    List<OrganizationRegionDataVO> listByRegionProvinceCityId(Long regionId, Long provinceId, Long cityId);

    public List<Long> getAreaIdList(Long regionId, Long provinceId, Long cityId);

    List<OrganizationSearchVO> listByTag(String tagName, Date createTimeBegin, Date createTimeEnd, Date updateTime, Long areaId);
}
