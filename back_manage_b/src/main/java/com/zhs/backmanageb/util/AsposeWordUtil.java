package com.zhs.backmanageb.util;

import com.aspose.words.*;
import com.zhs.backmanageb.exception.MyException;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhs
 * @date: 2020/8/9 11:52
 */
public class AsposeWordUtil {
    public static String getText(String fileName){
        String prefix;
        if(System.getProperty("os.name").toLowerCase().startsWith("win")){
            prefix = "c://data/file/";
        }else {
            prefix = "/data/file/";
        }
        File file = new File(prefix + fileName);
        if(!file.exists()){
            throw new MyException("文件不存在");
        }
        Document nodes;
        try {
            nodes = new Document(prefix + fileName);
        } catch (Exception e) {
            throw new MyException("word文件解析失败");
        }
        return nodes.getText();
    }


    public static   List<String> exportImageFromWordFile(String filePath, String savePath) throws Exception {
        File file = new File(filePath);
        if(!file.exists()){
            return new ArrayList<>();
        }
        //文件名集合
        List<String> list = new ArrayList<>();
        //加载word
        Document doc = new Document(filePath);
        NodeCollection<Shape> shapes = doc.getChildNodes(NodeType.SHAPE, true);
        int imageIndex = 0;
        for (Shape shape : shapes) {
            if (shape.hasImage())
            {
                //扩展名
                String ex = FileFormatUtil.imageTypeToExtension(shape.getImageData().getImageType());
                //文件名
                LocalDate now = LocalDate.now();
                String today = now.format(DateTimeFormatter.ofPattern("yyyy-M-d"));
                long currentTimeMillis = System.currentTimeMillis();
                String saveFilePath = today + "/" + currentTimeMillis + "/";
                String fileName = String.format("%s%s-%d%s",saveFilePath, System.currentTimeMillis(), imageIndex, ex);
                File file1 = new File(savePath+fileName);
                if(!file1.getParentFile().exists()){
                    file1.getParentFile().mkdirs();
                }
                shape.getImageData().save(savePath + fileName);
                //添加文件到集合
                list.add(fileName);
                imageIndex++;
            }
        }

        return list;
    }
}
