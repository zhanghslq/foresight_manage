package com.zhs.backmanageb.service;

import com.zhs.backmanageb.entity.OrganizationType;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhs.backmanageb.model.bo.OrganizationTypeBO;
import com.zhs.backmanageb.model.vo.CommonTypeVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
public interface OrganizationTypeService extends IService<OrganizationType> {


    /**
     * 查到所有的大的分类
     * @return
     */
    List<CommonTypeVO> listType();

    List<OrganizationTypeBO> listAll();

    /**
     * 查询所有组织，树状结构
     * @return
     */
    List<CommonTypeVO> listAllTree();

    /**
     * 批量添加
     * @param content
     * @param type
     * @param parentId
     * @param hasLocation
     */
    void insertBatch(String content, Integer type, Long parentId, Integer hasLocation);


    List<OrganizationTypeBO> listAllTreeByType(Integer type);

}
