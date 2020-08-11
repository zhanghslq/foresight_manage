package com.zhs.backmanageb.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.event.SyncReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.metadata.WriteSheet;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author: zhs
 * @date: 2020/2/22 10:18
 */
public class EasyExcelUtil {
    public static <T> List<T> readListFrom(InputStream is, Class<T> clz) {
        SyncReadListener tmpListener = new SyncReadListener();
        ReadSheet readSheet = new ReadSheet();
        readSheet.setClazz(clz);
        EasyExcel.read(is).registerReadListener(tmpListener).build().read(readSheet);
        return (List<T>) tmpListener.getList();
    }
    public static <T> void writeListTo(OutputStream os, List<T> data, Class<T> clz) {
        WriteSheet writeSheet = new WriteSheet();
        writeSheet.setClazz(clz);
        writeSheet.setNeedHead(true);
        ExcelWriter write = EasyExcel.write(os).build();
        write.write(data, writeSheet);
        write.finish();
    }
}