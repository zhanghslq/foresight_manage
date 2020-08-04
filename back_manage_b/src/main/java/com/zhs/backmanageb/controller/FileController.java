package com.zhs.backmanageb.controller;

import com.zhs.backmanageb.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author: zhs
 * @date: 2020/7/28 18:08
 */
@Slf4j
@RestController
@RequestMapping("file")
@Api(tags = "文件管理")
public class FileController {
    private  String prefix = "/data/file/";

    @GetMapping("getFile")
    @ApiOperation(value = "下载文件",tags = "查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileName",value = "返回的文件名字",required = true),
            @ApiImplicitParam(name = "isInline",value = "1预览，默认0下载")
    })
    public String getTemplate(HttpServletResponse response,@RequestParam String fileName,@RequestParam(defaultValue = "0",required = false) Integer isInline){
        if(System.getProperty("os.name").toLowerCase().startsWith("win")){
            prefix = "c://data/file/";
        }
        try {
            File file = new File(prefix + fileName);
            if(!file.exists()){
                return "文件不存在";
            }

            InputStream fis = new BufferedInputStream(new FileInputStream(prefix+fileName));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 设置response的Header
            response.setCharacterEncoding("utf-8");
            //1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
            if(isInline==1){
                response.setContentType("image/jpeg");
                response.setHeader("Content-Disposition", "inline; filename="
                        + new String(file.getName().getBytes(StandardCharsets.UTF_8), "iso8859-1"));
            }else {
                response.setContentType("multipart/form-data");
                response.setHeader("Content-Disposition", "attachment; filename="
                        + new String(file.getName().getBytes(StandardCharsets.UTF_8), "iso8859-1"));
            }

            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (IOException e) {
            e.printStackTrace();
            log.info("下载失败");
        }
        return null;
    }

    @ApiOperation(value = "上传文件,上传成功会把文件名返回，可以拿去下载",tags = "新增")
    @ApiImplicitParam(name = "file",value = "需要上传的文件",required = true)
    @PostMapping("upload")
    public Result<String> listUpload(@RequestParam("file") MultipartFile file){
        if(System.getProperty("os.name").toLowerCase().startsWith("win")){
            prefix = "c://data/file/";
        }
        if (file.isEmpty()) {
            return Result.fail(500,"文件不能为空","");
        }
        //批量添加
        String fileName = file.getOriginalFilename();
        if (StringUtils.isEmpty(fileName)){
            return Result.fail(500,"文件不能为空","");
        }

        // 定义文件路径 日期/时间戳/文件名
        LocalDate now = LocalDate.now();
        String today = now.format(DateTimeFormatter.ofPattern("yyyy-M-d"));
        long currentTimeMillis = System.currentTimeMillis();
        String filePath = today + "/" + currentTimeMillis + "/" + fileName;
        File file1 = new File(prefix + filePath);
        if(!file1.getParentFile().exists()){
            file1.getParentFile().mkdirs();
        }
        try {
            file.transferTo(file1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.success(filePath);
    }
}
