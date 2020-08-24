package com.zhs.backmanageb.aspose;

import com.aspose.words.*;
import com.zhs.backmanageb.util.AsposeWordUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhs
 * @date: 2020/8/9 8:19
 */
public class AsposeWordTest {

    @Test
    public void test() throws Exception {
        Document nodes = new Document("D:\\林武.docx");

        String text = nodes.getText();
        System.out.println(text);
    }
    @Test
    public void test2() {
        try {
            List<String> strings = AsposeWordUtil.exportImageFromWordFile("d://蔡奇.docx", "d://image//");
            System.out.println(strings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  List<String> ExportImageFromWordFile(String filePath, String savePath) throws Exception {
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
                String fileName = String.format("{}_{}{}", "time", imageIndex, ex);
                shape.getImageData().save(savePath + fileName);
                //添加文件到集合
                list.add(fileName);
                imageIndex++;
            }
        }

        return list;
    }


}
