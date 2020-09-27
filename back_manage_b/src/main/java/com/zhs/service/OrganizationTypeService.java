package com.zhs.service;

import com.zhs.entity.OrganizationType;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhs.model.bo.OrganizationTypeBO;
import com.zhs.model.vo.CommonTypeVO;
import com.zhs.model.vo.RootTypeVO;

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
    List<RootTypeVO> listAllTree();

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
