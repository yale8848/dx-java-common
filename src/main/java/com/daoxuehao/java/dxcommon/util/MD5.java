package com.daoxuehao.java.dxcommon.util;

import java.security.MessageDigest;

/**
 * Yale
 * create at: 2022-06-07 17:39
 **/
public class MD5 {

    public static String getMd5Str(String str) {
        byte[] digest = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            digest = md5.digest(str.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            int c = b & 0xFF ;
            if(c < 16){
                sb.append("0");
            }
            sb.append(Integer.toHexString(c));
        }
        return sb.toString();
    }
}
