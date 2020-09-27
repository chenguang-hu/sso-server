package com.learn.sso.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.InvalidKeyException;
import java.security.SecureRandom;

public class DES {

    private static Cipher getCipher(int mode) {

        // DES加密密钥key
        String key = "sdfDA12r3JHV214IJrwerDSO892BK2345nrekk35oewr4wrwrenlklknsdlemifzkw8iiiifegJG7649UJNDFJSvgsfdjFGDFGj435jUhjhjbkajb12kj987gsjh9834tbAXiudhf9B3PM4bt98dyf9Q2m97jjyf417aliD";

        Cipher cipher = null;

        try {
            // 首先，DES算法要求有一个可信任的随机数源，可以通过 SecureRandom类,内置两种随机数算法，NativePRNG和SHA1PRNG
            SecureRandom random = new SecureRandom();

            // 创建一个DESKeySpec对象
            DESKeySpec desKey = new DESKeySpec(key.getBytes());

            // 创建一个密匙工厂
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

            // 将DESKeySpec对象转换成SecretKey对象
            SecretKey securekey = keyFactory.generateSecret(desKey);

            // Cipher对象实际完成加密操作
            cipher = Cipher.getInstance("DES");

            cipher.init(mode, securekey, random);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return cipher;
    }

    /**
     * DES加密
     * @param plaintext 明文
     * @return String
     */
    public static String encryptDES(String plaintext) {

        String cipherText = null;

        try {
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);

            // 加密生成密文byte数组
            byte[] cipherBytes = cipher.doFinal(plaintext.getBytes());
            // 将密文byte数组转化为16进制密文
            cipherText = byteToHex(cipherBytes);

        } catch (Throwable e) {
            e.printStackTrace();
        }

        return cipherText;
    }

    public static String decryptDES(String ciphertext) {

        String plainText = null;

        try {

            Cipher cipher = getCipher(Cipher.DECRYPT_MODE);

            // 将16进制密文转化为密文byte数组
            byte[] cipherBytes = hexToByte(ciphertext);
            // 真正开始解密操作
            plainText = new String(cipher.doFinal(cipherBytes));

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return plainText;
    }

    // 将byte转化为16进制
    private static String byteToHex(byte[] bs) {
        if (0 == bs.length) {
            return "";
        } else {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < bs.length; i++) {
                String s = Integer.toHexString(bs[i] & 0xFF);
                if (1 == s.length()) {
                    sb.append("0");
                }
                sb = sb.append(s.toUpperCase());
            }
            return sb.toString();
        }
    }

    // 将16进制转化为byte
    private static byte[] hexToByte(String cipherText) {
        byte[] cipherBytes = cipherText.getBytes();
        if ((cipherBytes.length % 2) != 0) {
            throw new IllegalArgumentException("长度不为偶数");
        } else {
            byte[] result = new byte[cipherBytes.length / 2];
            for (int i = 0; i < cipherBytes.length; i += 2) {
                String item = new String(cipherBytes, i, 2);
                result[i / 2] = (byte) Integer.parseInt(item, 16);
            }
            return result;
        }
    }

}
