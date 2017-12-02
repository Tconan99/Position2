package com.jc.position2.base.network;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by tconan on 16/4/26.
 * 有问题，死循环。。。
 */
public class SecurityUtil {
    static final String algorithmStr = "AES/ECB/PKCS5Padding";

    static private KeyGenerator keyGen;

    static private Cipher cipher;

    static boolean isInited = false;

    // 初始化
    static private void init() {

        // 初始化keyGen
        try {
            keyGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyGen.init(128);

        // 初始化cipher
        try {
            cipher = Cipher.getInstance(algorithmStr);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        isInited = true;
    }

    public static byte[] GenKey() {
        if (!isInited)// 如果没有初始化过,则初始化
        {
            init();
        }
        return keyGen.generateKey().getEncoded();
    }

    /**
     * 加密
     * @param content
     * @return
     */
    public static String Encrypt(String content, String aesKey) {

        //为空时不做处理
        if(null == content || "".equals(content)){
            return content;
        }

        byte[] encryptedText = null;

        if (!isInited)// 为初始化
        {
            init();
        }

        Key key = new SecretKeySpec(aesKey.getBytes(), "AES");

        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        try {
            encryptedText = cipher.doFinal(content.getBytes("utf-8"));
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return byte2hex(encryptedText);
    }

    /**
     * 解密
     * @param content
     * @return
     */
    public static String DecryptToString(String content, String aesKey) {

        //为空时不做处理
        if(null == content || "".equals(content)){
            return content;
        }

        byte[] originBytes = null;
        if (!isInited) {
            init();
        }

        Key key = new SecretKeySpec(aesKey.getBytes(), "AES");

        // 解密
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            originBytes = cipher.doFinal(hex2byte(content));
        }catch (InvalidKeyException e) {
            e.printStackTrace();
        }catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return new String(originBytes);
    }

    /**
     * 十六进制转二进制
     * @param strhex
     * @return
     */
    public static byte[] hex2byte(String strhex) {
        if (strhex == null) {
            return null;
        }
        int l = strhex.length();
        if (l % 2 == 1) {
            return null;
        }
        byte[] b = new byte[l / 2];
        for (int i = 0; i != l / 2; i++) {
            b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2),
                    16);
        }
        return b;
    }

    /**
     * 二进制转十六进制
     * @param b
     * @return
     */
    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs;
    }

    /**
     * 返回16位长度的字符串作为加密KEY值
     * @param converString
     * @return
     */
    public static String fillString(String converString){

        //为空的时候返回空字符串
        if("".equals(converString) || null == converString){
            return "";
        }

        //大于16位长度的时候自动截取
        if(converString.length()>16){
            return converString.substring(0, 16);
        }

        //不足16位长度的时候补足16位
        while(converString.length()<16){
            converString += "0";
        }

        return converString;
    }
}
