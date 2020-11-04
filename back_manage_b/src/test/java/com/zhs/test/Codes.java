package com.zhs.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

public class Codes {
    public static List<String> lists = new ArrayList<>();

    /**
     * 解析读取二维码
     * 
     * @param qrCodePath 二维码图片路径
     * @return
     */
    public static String decodeQRcode(String qrCodePath) {
        BufferedImage image;
        String qrCodeText = null;
        try {
            image = ImageIO.read(new File(qrCodePath));
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            Binarizer binarizer = new HybridBinarizer(source);
            BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
            Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>();
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
            // 对图像进行解码
            com.google.zxing.Result result = new MultiFormatReader().decode(binaryBitmap, hints);
            ResultPoint[] resultPoints = result.getResultPoints();
            for (ResultPoint resultPoint : resultPoints) {
                float x = resultPoint.getX();
                float y = resultPoint.getY();
                System.out.println(x+","+y);
            }
            qrCodeText = result.getText();
        } catch (IOException | NotFoundException e) {
            e.printStackTrace();
        }
        return qrCodeText;
    }

    public static void qu(String path) {
        File f = new File(path);
        String[] listFiles = f.list();
        for (String filePath : listFiles) {
            String decodeQRcode = Codes.decodeQRcode(path + "\\" + filePath);
            lists.add(decodeQRcode);
        }
    }

    public static void main(String[] args) {
        Codes.qu("D:\\code");
        System.out.println(lists);
    }
}
 