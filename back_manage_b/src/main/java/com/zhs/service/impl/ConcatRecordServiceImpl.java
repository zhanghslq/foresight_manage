package com.zhs.service.impl;

import com.zhs.exception.MyException;
import com.zhs.mapper.ConcatRecordMapper;
import com.zhs.model.vo.ConcatRecordVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhs.entity.Admin;
import com.zhs.entity.ConcatRecord;
import com.zhs.entity.Contacts;
import com.zhs.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 * 联系记录表 服务实现类
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Service
public class ConcatRecordServiceImpl extends ServiceImpl<ConcatRecordMapper, ConcatRecord> implements ConcatRecordService {

    @Autowired
    private ConcatRecordMapper concatRecordMapper;
    @Autowired
    private ContactsService contactsService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private OrganizationService organizationService;


    @Override
    public ConcatRecordVO queryDetail(Long id) {
        ConcatRecord concatRecord = concatRecordMapper.selectById(id);

        if(Objects.isNull(concatRecord)){
            throw new MyException("查询记录不存在");
        }
        Contacts contacts = contactsService.getById(concatRecord.getConcatPersonId());
        //Contacts contacts = contactsService.getById(concatRecord.getConcatPersonId());
        ConcatRecordVO concatRecordVO = new ConcatRecordVO(concatRecord);
        // Long companyId = concatRecord.getCompanyId();
        /*if(!Objects.isNull(companyId)){
            // 需要考虑单位到底存在哪个表，企业和政府是否有区别
            // 联系人默认都是政府的领导的联系人，企业无，如果企业需要的话，那联系人需要做标志
            if(contacts.getIsCompany()==1){
                Company company = companyService.getById(companyId);
                concatRecordVO.setCompanyName(company.getName());
            }else {
                Organization organization = organizationService.getById(companyId);
                concatRecordVO.setCompanyName(organization.getName());
            }

        }*/
        concatRecordVO.setCompanyName(contacts.getCompanyName());
        Admin admin = adminService.getById(concatRecord.getOperatorId());
        concatRecordVO.setOperatorName(admin.getRealName());
        return concatRecordVO;
    }
}
