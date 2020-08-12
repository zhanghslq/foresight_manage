package com.zhs.backmanageb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhs.backmanageb.common.constant.DropDownBoxTypeEnum;
import com.zhs.backmanageb.entity.CommonData;
import com.zhs.backmanageb.entity.Expert;
import com.zhs.backmanageb.exception.MyException;
import com.zhs.backmanageb.mapper.ExpertMapper;
import com.zhs.backmanageb.model.bo.CommonCountBO;
import com.zhs.backmanageb.model.vo.InputStatisticsVO;
import com.zhs.backmanageb.service.CommonDataService;
import com.zhs.backmanageb.service.ExpertService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private CommonDataService commonDataService;

    @Autowired
    private ExpertMapper expertMapper;
    @Override
    public void saveBatchSelf(Long classificationId, List<Expert> readBooks) {
        CommonData byId = commonDataService.getById(classificationId);
        if(Objects.isNull(byId)){
            throw new MyException("专家类别不存在");
        }
        // 需要对字段进行处理，id，name等的
        QueryWrapper<CommonData> commonDataQueryWrapper = new QueryWrapper<>();
        commonDataQueryWrapper.eq("type", DropDownBoxTypeEnum.EXPERT_LEVEL.getId());

        Subject subject = SecurityUtils.getSubject();
        Object principal = subject.getPrincipal();
        Long adminId=0L;
        try {
            adminId = Long.valueOf(principal.toString());
        } catch (NumberFormatException e) {
            log.error("未获取到认证信息");
        }
        List<CommonData> list = commonDataService.list(commonDataQueryWrapper);
        Map<String, Long> map = list.stream().collect(Collectors.toMap(CommonData::getName, CommonData::getId, (k1, k2) -> k2));


        QueryWrapper<CommonData> commonDataFieldQueryWrapper = new QueryWrapper<>();
        commonDataFieldQueryWrapper.eq("type", DropDownBoxTypeEnum.EXPERT_LEVEL.getId());
        List<CommonData> listField = commonDataService.list(commonDataQueryWrapper);
        Map<String, Long> mapField = listField.stream().collect(Collectors.toMap(CommonData::getName, CommonData::getId, (k1, k2) -> k2));

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
                readBook.setLevelId(map.get(levelName));
            }
            String workArea = readBook.getWorkArea();
            if(!Objects.isNull(workArea)){
                readBook.setWorkAreaId(mapField.get(workArea));
            }
            readBook.setId(null);
        }
        saveBatch(readBooks);
    }

    @Override
    public List<InputStatisticsVO> expertInputStatistics() {
        QueryWrapper<CommonData> commonDataQueryWrapper = new QueryWrapper<>();
        commonDataQueryWrapper.eq("type",DropDownBoxTypeEnum.EXPERT_CLASSIFICATION.getId());
        List<CommonData> list = commonDataService.list(commonDataQueryWrapper);
        List<CommonCountBO> commonCountBOS = expertMapper.countByClassificationId();
        Map<Long, Integer> map = commonCountBOS.stream().collect(Collectors.toMap(CommonCountBO::getId, CommonCountBO::getCount, (k1, k2) -> k2));
        ArrayList<InputStatisticsVO> result = new ArrayList<>();
        for (CommonData commonData : list) {
            InputStatisticsVO inputStatisticsVO = new InputStatisticsVO();
            inputStatisticsVO.setId(commonData.getId());
            inputStatisticsVO.setName(commonData.getName());
            inputStatisticsVO.setCount(map.get(commonData.getId()));
            result.add(inputStatisticsVO);
        }
        return result;
    }
}
