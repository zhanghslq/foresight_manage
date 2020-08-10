package com.zhs.backmanageb.util;

import com.aspose.words.Document;
import com.zhs.backmanageb.exception.MyException;

import java.io.File;

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
}
