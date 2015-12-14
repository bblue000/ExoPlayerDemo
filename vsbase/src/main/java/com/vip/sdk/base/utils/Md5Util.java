package com.vip.sdk.base.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {

    /**
     * 生成md5校验码
     *
     * @param srcContent 需要加密的数据
     * @return 加密后的md5校验码。出错则返回null。
     */
    public static String makeMd5Sum( byte[] srcContent ) {
        if ( srcContent == null ) {
            return null;
        }

        String strDes = null;

        try {
            MessageDigest md5 = MessageDigest.getInstance( "MD5" );
            md5.update( srcContent );
            strDes = bytes2Hex( md5.digest() ); // to HexString
        } catch ( NoSuchAlgorithmException e ) {
            return null;
        }
        return strDes;
    }

    /**
     * bytes2Hex方法
     */
    public static String bytes2Hex( byte[] byteArray ) {
        StringBuffer strBuf = new StringBuffer();
        int bytelength = byteArray.length;
        for ( int i = 0; i < bytelength; i++ ) {
            if ( byteArray[i] >= 0 && byteArray[i] < 16 ) {
                strBuf.append( "0" );
            }
            strBuf.append( Integer.toHexString( byteArray[i] & 0xFF ) );
        }
        return strBuf.toString();
    }

    public static String makeMD5(String src) {
        if(src == null || src.equals("")){
            return "";
        }
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(src.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10){
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
}
