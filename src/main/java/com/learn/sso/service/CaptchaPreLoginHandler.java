package com.learn.sso.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service("captchaPre")
public class CaptchaPreLoginHandler implements IPreLoginHandler {

    private final static String CODES = "0123456789";
    private final static int LEN = 4;

    @Override
    public Map<String, Object> handle(HttpSession session) throws Exception {
        Map<String, Object> ret = new HashMap<>();
        // 生成随机码
        String code = randomCode();

        // 放入session
        session.setAttribute(SESSINO_ATTR_NAME, code);

        ret.put("imgData", "data:image/png;base64," + Base64.getEncoder().encodeToString(generateImg(code)));

        return ret;
    }

    /**
     * 4位随机数字字符串
     *
     * @return
     */
    private String randomCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < LEN; i++) {
            sb.append(CODES.charAt(random.nextInt(CODES.length())));
        }
        return sb.toString();
    }

    private byte[] generateImg(String code) throws IOException {

        final int width = 75;
        final int height = 30;

        BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = bimg.createGraphics();

        // 绘制背景
        graphics2D.setColor(Color.WHITE);
        graphics2D.fillRect(0, 0, width, height);

        graphics2D.setColor(Color.GRAY);
        graphics2D.setFont(new Font("黑体", Font.BOLD, 25));

        // 绘制干扰线
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);

            graphics2D.drawLine(x1, y1, x2, y2);
        }

        for (int i = 0; i < LEN; i++) {
            graphics2D.drawString(String.valueOf(code.charAt(i)), 5 + 16 * i, 25);
        }

        // 释放资源
        graphics2D.dispose();

        // 输出
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bimg, "png", byteArrayOutputStream);
        byteArrayOutputStream.close();

        return byteArrayOutputStream.toByteArray();
    }
}
