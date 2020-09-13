package com.zhs.service.impl;

import com.zhs.entity.ResumeCompany;
import com.zhs.mapper.ResumeCompanyMapper;
import com.zhs.service.ResumeCompanyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 简历对应的多个单位，多个职务问题，跟企业表无关系 服务实现类
 * </p>
 *
 * @author zhs
 * @since 2020-08-17
 */
@Service
public class ResumeCompanyServiceImpl extends ServiceImpl<ResumeCompanyMapper, ResumeCompany> implements ResumeCompanyService {

}
