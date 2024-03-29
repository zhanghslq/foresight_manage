package com.zhs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhs.common.constant.DownBoxTypeEnum;
import com.zhs.common.constant.ScopeEnum;
import com.zhs.entity.ConcatRecord;
import com.zhs.entity.Contacts;
import com.zhs.entity.DownBoxData;
import com.zhs.entity.Expert;
import com.zhs.exception.MyException;
import com.zhs.mapper.ExpertMapper;
import com.zhs.model.bo.CommonCountBO;
import com.zhs.model.vo.InputStatisticsVO;
import com.zhs.service.ConcatRecordService;
import com.zhs.service.DownBoxDataService;
import com.zhs.service.ExpertService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 专家 服务实现类
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Service
public class ExpertServiceImpl extends ServiceImpl<ExpertMapper, Expert> implements ExpertService {


    @Autowired
    private DownBoxDataService downBoxDataService;
    @Autowired
    private ExpertMapper expertMapper;
    @Autowired
    private ConcatRecordService concatRecordService;

    @Override
    public void saveBatchSelf(Long classificationId, List<Expert> readBooks) {
        DownBoxData byId = downBoxDataService.getById(classificationId);
        if(Objects.isNull(byId)){
            throw new MyException("专家类别不存在");
        }
        // 需要对字段进行处理，id，name等的

        Subject subject = SecurityUtils.getSubject();
        Object principal = subject.getPrincipal();
        Long adminId=0L;
        try {
            adminId = Long.valueOf(principal.toString());
        } catch (NumberFormatException e) {
            log.error("未获取到认证信息");
        }
        List<DownBoxData> list = downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.EXPERT_LEVEL.getId(),null);
        Map<String, Integer> map = list.stream().collect(Collectors.toMap(DownBoxData::getName, DownBoxData::getId, (k1, k2) -> k2));

        List<DownBoxData> listField = downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.EXPERT_LEVEL.getId(),null);
        Map<String, Integer> mapField = listField.stream().collect(Collectors.toMap(DownBoxData::getName, DownBoxData::getId, (k1, k2) -> k2));

        for (Expert readBook : readBooks) {
            readBook.setClassificationId(classificationId);
            readBook.setClassificationName(byId.getName());
            readBook.setAdminId(adminId);
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
                Integer integer = map.get(levelName);
                if(!Objects.isNull(integer)){
                    readBook.setLevelId(integer.longValue());
                }
            }
            String workArea = readBook.getWorkArea();
            if(!Objects.isNull(workArea)){
                Integer integer = mapField.get(workArea);
                if(!Objects.isNull(integer)){
                    readBook.setWorkAreaId(integer.longValue());
                }
            }
            readBook.setId(null);
        }
        saveBatch(readBooks);
    }

    @Override
    public List<InputStatisticsVO> expertInputStatistics() {
        List<DownBoxData> list = downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.EXPERT_CLASSIFICATION.getId(), ScopeEnum.EXPERT.getId());
        List<CommonCountBO> commonCountBOS = expertMapper.countByClassificationId();
        Map<Long, Integer> map = commonCountBOS.stream().collect(Collectors.toMap(CommonCountBO::getId, CommonCountBO::getCount, (k1, k2) -> k2));
        ArrayList<InputStatisticsVO> result = new ArrayList<>();
        for (DownBoxData commonData : list) {
            InputStatisticsVO inputStatisticsVO = new InputStatisticsVO();
            inputStatisticsVO.setId(commonData.getId().longValue());
            inputStatisticsVO.setName(commonData.getName());
            inputStatisticsVO.setCount(map.get(commonData.getId().longValue()));
            result.add(inputStatisticsVO);
        }
        return result;
    }

    @Override
    public Page<Expert> listContactExpert(Long adminId, Integer current, Integer size) {
        Page<Expert> result = new Page<>();
        QueryWrapper<ConcatRecord> concatRecordQueryWrapper = new QueryWrapper<>();
        concatRecordQueryWrapper.eq("operator_id",adminId);
        concatRecordQueryWrapper.eq("person_type",1);
        Page<ConcatRecord> concatRecordPage = new Page<>(current,size);
        Page<ConcatRecord> page = concatRecordService.page(concatRecordPage, concatRecordQueryWrapper);
        List<ConcatRecord> records = page.getRecords();

        result.setCurrent(page.getCurrent());
        result.setSize(page.getSize());
        result.setTotal(page.getTotal());

        if(records.size()>0){
            List<Long> expertIdList = records.stream().map(ConcatRecord::getConcatPersonId).collect(Collectors.toList());
            Map<Long, List<ConcatRecord>> map = records.stream().filter(concatRecord -> Objects.nonNull(concatRecord.getConcatPersonId())).collect(Collectors.groupingBy(ConcatRecord::getConcatPersonId));
            List<Expert> expertList = listByIds(expertIdList);
            for (Expert expert : expertList) {
                List<ConcatRecord> concatRecords = map.get(expert.getId());
                if(Objects.nonNull(concatRecords)){
                    List<Date> collect = concatRecords.stream().map(ConcatRecord::getCreateTime).collect(Collectors.toList());
                    Date max = Collections.max(collect);
                    expert.setContactTime(max);
                }
            }
            result.setRecords(expertList);
        }
        return result;
    }
}
