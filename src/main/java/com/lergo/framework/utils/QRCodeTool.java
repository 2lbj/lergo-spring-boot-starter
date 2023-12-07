package com.lergo.framework.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class QRCodeTool {

    /**
     * 生成QR码
     *
     * @param data
     * @return Base64
     */
    public static String generateQRCodeBase64(String data) {
        return generateQRCodeBase64(data, 300, 300);
    }

    public static String generateQRCodeBase64(String data, int width, int height) {
        try {
            // 生成二维码
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); // 设置字符编码
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // 错误纠正级别
            hints.put(EncodeHintType.MARGIN, 1); // 二维码边距

            BitMatrix bitMatrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, width, height, hints);

            // 创建BufferedImage对象来表示QR码
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
                }
            }

            // 转为Base64
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "png", os);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(os.toByteArray());

        } catch (WriterException | IOException e) {
            return e.getMessage();
        }
    }

    /**
     * 生成条形码
     *
     * @param data
     * @return Base64
     */
    public static String generateBarcodeBase64(String data) {
        return generateBarcodeBase64(data, 200, 100);
    }

    public static String generateBarcodeBase64(String data, int width, int height) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); // 设置字符编码

            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.CODE_128, width, height, hints);

            // 创建BufferedImage对象来表示条形码
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? 0 : 0xFFFFFF); // 生成黑色条和白色背景的条形码
                }
            }

            // 转为Base64
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "png", os);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(os.toByteArray());
        } catch (WriterException | IOException e) {
            return e.getMessage();
        }
    }
}
