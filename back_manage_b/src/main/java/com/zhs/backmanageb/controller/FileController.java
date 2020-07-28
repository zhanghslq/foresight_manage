package com.zhs.backmanageb.controller;

import com.zhs.backmanageb.common.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    @RequestMapping("getFile")
    public String getTemplate(HttpServletResponse response,String fileName){
        if(System.getProperty("os").toLowerCase().startsWith("win")){
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
            response.setContentType("multipart/form-data");

            response.setHeader("Content-Disposition", "attachment; filename="
                    + new String(file.getName().getBytes(StandardCharsets.UTF_8), "iso8859-1"));
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


    @RequestMapping("upload")
    public Result<String> listUpload(@RequestParam("file") MultipartFile file){
        if(System.getProperty("os").toLowerCase().startsWith("win")){
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
        String name = file.getName();
        String filename = today + "/" + currentTimeMillis + "/" + name;
        File file1 = new File(prefix + fileName);
        if(!file1.getParentFile().exists()){
            file1.getParentFile().mkdirs();
        }
        try {
            file.transferTo(file1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.success(fileName);
    }
}
