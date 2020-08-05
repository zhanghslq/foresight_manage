package com.zhs.backmanageb.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhs.backmanageb.common.constant.DropDownBoxTypeEnum;
import com.zhs.backmanageb.entity.CommonData;
import com.zhs.backmanageb.entity.Resume;
import com.zhs.backmanageb.mapper.ResumeMapper;
import com.zhs.backmanageb.model.vo.ResumeVO;
import com.zhs.backmanageb.service.CommonDataService;
import com.zhs.backmanageb.service.ResumeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.sf.jsqlparser.statement.drop.Drop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Service
public class ResumeServiceImpl extends ServiceImpl<ResumeMapper, Resume> implements ResumeService {

    @Autowired
    private CommonDataService commonDataService;

    @Override
    public Page<ResumeVO> pageSelf(Page<Resume> resumePage) {
        QueryWrapper<CommonData> commonDataQueryWrapper = new QueryWrapper<>();
        commonDataQueryWrapper.eq("type", DropDownBoxTypeEnum.RESUME_LEVEL.getId());
        List<CommonData> list = commonDataService.list(commonDataQueryWrapper);
        Map<Long, String> map = list.stream().collect(Collectors.toMap(CommonData::getId, CommonData::getName));
        List<ResumeVO> resumeVOS = new ArrayList<>();
        Page<ResumeVO> resumeVOPage = new Page<>();
        Page<Resume> page = page(resumePage);
        // 对page进行处理
        List<Resume> records = page.getRecords();
        BeanUtil.copyProperties(page,resumeVOPage);
        for (Resume record : records) {
            ResumeVO resumeVO = new ResumeVO();
            BeanUtil.copyProperties(record,resumeVO);
            // 然后把字段值填上
            // 行政级别
            resumeVO.setLevelName(map.get(resumeVO.getLevelId()));
            resumeVOS.add(resumeVO);
            //todo 年龄，在职时间
        }
        resumeVOPage.setRecords(resumeVOS);
        return resumeVOPage;
    }
}
