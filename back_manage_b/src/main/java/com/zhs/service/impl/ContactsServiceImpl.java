package com.zhs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhs.common.constant.DownBoxTypeEnum;
import com.zhs.common.constant.DropDownBoxTypeEnum;
import com.zhs.common.constant.ScopeEnum;
import com.zhs.entity.CommonData;
import com.zhs.entity.Contacts;
import com.zhs.entity.DownBoxData;
import com.zhs.mapper.ContactsMapper;
import com.zhs.service.CommonDataService;
import com.zhs.service.ContactsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhs.service.DownBoxDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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
                readBook.setLevelId(map.get(levelName).longValue());
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
}
