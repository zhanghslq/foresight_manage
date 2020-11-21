package com.zhs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhs.common.constant.DownBoxTypeEnum;
import com.zhs.common.constant.DropDownBoxTypeEnum;
import com.zhs.common.constant.ScopeEnum;
import com.zhs.entity.*;
import com.zhs.mapper.ContactsMapper;
import com.zhs.service.CommonDataService;
import com.zhs.service.ConcatRecordService;
import com.zhs.service.ContactsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhs.service.DownBoxDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 联系人 服务实现类
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Service
public class ContactsServiceImpl extends ServiceImpl<ContactsMapper, Contacts> implements ContactsService {

    @Autowired
    private ContactsMapper contactsMapper;

    @Autowired
    private DownBoxDataService downBoxDataService;

    @Autowired
    private ConcatRecordService concatRecordService;

    @Override
    public void saveBatchSelf(List<Contacts> readBooks) {
        // 需要对字段进行处理，id，name等的

        List<DownBoxData> list = downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.ORGANIZATION_LEVEL.getId(), ScopeEnum.CONTACTS.getId());
        Map<String, Integer> map = list.stream().collect(Collectors.toMap(DownBoxData::getName, DownBoxData::getId, (k1, k2) -> k2));

        for (Contacts readBook : readBooks) {
            String sexName = readBook.getSexName();
            if(!Objects.isNull(sexName)){
                if(sexName.contains("男")){
                    readBook.setSex(1);
                }else if(sexName.contains("女")){
                    readBook.setSex(0);
                }
            }
            String levelName = readBook.getLevelName();
            if(!Objects.isNull(levelName)){
                Integer levelId = map.get(levelName);
                if(!Objects.isNull(levelId)){
                    readBook.setLevelId(levelId.longValue());
                }
            }
            readBook.setId(null);
        }
        saveBatch(readBooks);
    }

    @Override
    public void bindingOrganization(Integer isCompany, Long organizationId, List<Long> contactIds) {
        // 先放一下，待确定
        if(isCompany==1){
            // 企业

        }else {
            // 组织

        }

    }

    @Override
    public Page<Contacts> listContactExpert(Long adminId, Integer current, Integer size) {
        Page<Contacts> result = new Page<>();
        QueryWrapper<ConcatRecord> concatRecordQueryWrapper = new QueryWrapper<>();
        concatRecordQueryWrapper.eq("operator_id",adminId);
        concatRecordQueryWrapper.eq("person_type",0);
        Page<ConcatRecord> concatRecordPage = new Page<>(current,size);
        Page<ConcatRecord> page = concatRecordService.page(concatRecordPage, concatRecordQueryWrapper);
        List<ConcatRecord> records = page.getRecords();

        result.setCurrent(page.getCurrent());
        result.setSize(page.getSize());
        result.setTotal(page.getTotal());

        if(records.size()>0){
            List<Long> expertIdList = records.stream().map(ConcatRecord::getConcatPersonId).collect(Collectors.toList());
            Map<Long, List<ConcatRecord>> map = records.stream().filter(concatRecord -> Objects.nonNull(concatRecord.getConcatPersonId())).collect(Collectors.groupingBy(ConcatRecord::getConcatPersonId));
            List<Contacts> expertList = listByIds(expertIdList);
            for (Contacts contacts : expertList) {
                List<ConcatRecord> concatRecords = map.get(contacts.getId());
                if(Objects.nonNull(concatRecords)){
                    List<Date> collect = concatRecords.stream().map(ConcatRecord::getCreateTime).collect(Collectors.toList());
                    Date max = Collections.max(collect);
                    contacts.setContactTime(max);
                }
            }

            result.setRecords(expertList);
        }
        return result;

    }
}
