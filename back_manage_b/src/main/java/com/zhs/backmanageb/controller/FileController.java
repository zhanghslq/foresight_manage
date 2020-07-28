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

/**
 * @author: zhs
 * @date: 2020/7/28 18:08
 */
@Slf4j
@RestController
@RequestMapping("file")
@Api(tags = "文件管理")
public class FileController {

    @RequestMapping("getTemplate")
    public void getTemplate(HttpServletResponse response,String fileName){
        try {
            InputStream fis = new BufferedInputStream(new FileInputStream("主持人批量导入模板.xlsx"));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 设置response的Header
            response.setCharacterEncoding("utf-8");

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            response.setHeader("Content-Disposition", "attachment; filename="
                    + new String("主持人批量导入模板".getBytes(StandardCharsets.UTF_8), "iso8859-1")
                    + ".xlsx");
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (IOException e) {
            e.printStackTrace();
            log.info("下载失败");
        }
    }


    @RequestMapping("listUpload")
    public Result<String> listUpload(@RequestParam("file") MultipartFile file){
        //批量添加
        String fileName = file.getOriginalFilename();
        if (StringUtils.isEmpty(fileName)){
            return Result.fail(500,"文件不能为空","");
        }
        // 获取文件后缀
        String prefix=fileName.substring(fileName.lastIndexOf("."));
        if (!prefix.toLowerCase().contains("xls") && !prefix.toLowerCase().contains("xlsx") ){
            return Result.fail(500,"文件格式异常，请上传Excel文件格式","");
        }
        // 防止生成的临时文件重复-建议使用UUID
        final File excelFile;
        try {
            excelFile = File.createTempFile(System.currentTimeMillis()+"", prefix);
            file.transferTo(excelFile);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail(500,"文件上传失败","");
        }

        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(excelFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return Result.fail(500,"文件上传失败","");
        }

        return Result.success("");
    }
}
