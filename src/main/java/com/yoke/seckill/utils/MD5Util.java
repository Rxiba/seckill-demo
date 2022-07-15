package com.yoke.seckill.utils;

/**
 * @Author A_xiba
 * @Date 2022/7/1 16:56
 * @Version 1.0
 */

import lombok.val;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * MD5工具类
 */
public class MD5Util {

    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    private static final String salt = "1a2b3c4d";

    public static String inputPassToFromPass(String inputPass){
        String str = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String fromPassToDBPass(String fromPass, String salt){
        String str = "" + salt.charAt(0) + salt.charAt(2) + fromPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDBPass(String inputPass, String saltDB){
        String fromPass = inputPassToFromPass(inputPass);
        String dbPass = fromPassToDBPass(fromPass, saltDB);
        return dbPass;
    }

    public static void main(String[] args) {
        System.out.println(inputPassToFromPass("123456"));//d3b1294a61a07da9b49b6e22b2cbd7f9
        System.out.println(fromPassToDBPass("d3b1294a61a07da9b49b6e22b2cbd7f9","1a2b3c4d"));
        System.out.println(inputPassToDBPass("123456","1a2b3c4d"));//b7797cce01b4b131b433b6acf4add449
    }
}
