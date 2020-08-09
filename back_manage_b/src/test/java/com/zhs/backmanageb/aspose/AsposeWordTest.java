package com.zhs.backmanageb.aspose;

import com.aspose.words.Document;
import com.aspose.words.FileFormatUtil;
import com.aspose.words.NodeCollection;
import org.junit.jupiter.api.Test;

import java.io.File;
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


}
