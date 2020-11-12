package com.zhs.test;

import cn.hutool.core.io.FileUtil;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * @author: zhs
 * @since: 2020/11/11 15:44
 */
public class DirectoryTest {

    @Test
    public void test(){
        File file = new File("D:\\BaiduNetdiskDownload");
        dealDirectory(file);
    }

    public void dealDirectory(File file){
        if(!file.exists()){
            return;
        }
        if(!file.isDirectory()){
            return;
        }
        File[] files = file.listFiles();
        for (File fileSon : files) {
            if(fileSon.isDirectory()){
                dealDirectory(fileSon);
            }else {
                System.out.println(fileSon.getAbsolutePath());
                System.out.println(fileSon.getName());
//                FileUtil.copy("","",false);
                // 拷贝之后需要分析

                // 分析出错的放到一个文件夹
            }
        }
    }

}
