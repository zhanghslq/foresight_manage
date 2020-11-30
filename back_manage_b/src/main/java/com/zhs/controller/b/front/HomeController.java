package com.zhs.controller.b.front;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhs.common.Result;
import com.zhs.entity.ConcatRecord;
import com.zhs.model.vo.HomeVO;
import com.zhs.service.ConcatRecordService;
import com.zhs.service.ContactsService;
import com.zhs.service.ExpertService;
import com.zhs.service.LeaderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.actuate.cache.NonUniqueCacheException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: zhs
 * @since: 2020/11/27 18:23
 */
@RequestMapping("/home")
@RestController
@Api(tags = "首页数据")
public class HomeController {

    @Resource
    private LeaderService leaderService;
    @Resource
    private ContactsService contactsService;
    @Resource
    private ExpertService expertService;
    @Resource
    private ConcatRecordService concatRecordService;



    @PostMapping("get/base_count")
    @ApiOperation(value = "首页的统计数据",tags = "查询")
    public Result<HomeVO> get(Long adminId){
        HomeVO homeVO = new HomeVO();
        int expertCount = expertService.count();
        int leaderCount = leaderService.count();
        int contactsCount = contactsService.count();

        QueryWrapper<ConcatRecord> concatRecordQueryWrapper = new QueryWrapper<>();
        concatRecordQueryWrapper.eq("operator_id",adminId);
        concatRecordQueryWrapper.eq("person_type",0);
        int myContactsCount = concatRecordService.count(concatRecordQueryWrapper);

        QueryWrapper<ConcatRecord> leaderConcatRecordQueryWrapper = new QueryWrapper<>();
        leaderConcatRecordQueryWrapper.eq("operator_id",adminId);
        leaderConcatRecordQueryWrapper.eq("person_type",1);
        int myExpertCount = concatRecordService.count(leaderConcatRecordQueryWrapper);

        homeVO.setTotalContactsCount(contactsCount);
        homeVO.setTotalExpertCount(expertCount);
        homeVO.setTotalLeaderCount(leaderCount);
        homeVO.setMyContactsCount(myContactsCount);
        homeVO.setMyExpertCount(myExpertCount);
        return Result.success(homeVO);
    }


}
