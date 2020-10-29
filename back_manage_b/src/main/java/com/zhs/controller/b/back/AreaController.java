package com.zhs.controller.b.back;


import com.alibaba.fastjson.JSON;
import com.zhs.common.Result;
import com.zhs.model.bo.AreaBO;
import com.zhs.service.AreaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.*;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-07-26
 */
@Api(tags = "地区获取")
@RestController
@RequestMapping("/area")
public class AreaController {

    @Resource
    private AreaService areaService;

    @ApiOperation("查询所有地区，树状结构")
    @PostMapping("select/all_tree")
    public Result<List<AreaBO>> selectAllTree(){
        return Result.success(areaService.selectAllTree());
    }

    @ApiOperation(value = "查询所有地区，不查数据库",tags = "查询")
    @PostMapping("select/all_tree_no_db")
    public Result<List<AreaBO>> selectAllNoDatabase(){
        List<AreaBO> areaBOS = JSON.parseArray(readFileToString(), AreaBO.class);
        return Result.success(areaBOS);
    }
    private static String readFileToString() {
        // new 一个空文件，用于获取路径
        BufferedReader reader = null;
        StringBuilder fileData = null;
        try {
            fileData = new StringBuilder(1000);
            InputStream resourceAsStream = AreaController.class.getResourceAsStream("/area.json");
            reader = new BufferedReader(new InputStreamReader(resourceAsStream));
            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[1024];
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != reader){
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileData.toString();
    }
}

