package com.vip.sdk.base.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHAUtil {

    /**
     * 生成sha校验码
     *
     * @param srcContent 需要加密的数据
     * @return 加密后的sha校验码。出错则返回null。
     */
    public static String makeShaSum(byte[] srcContent) {
        if (srcContent == null) {
            return null;
        }

        String strDes = null;

        try {
            MessageDigest sha = MessageDigest.getInstance("SHA1");
            sha.update(srcContent);
            strDes = bytes2Hex(sha.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return strDes;
    }

    /**
     * bytes2Hex方法
     */
    private static String bytes2Hex(byte[] byteArray) {
        StringBuffer strBuf = new StringBuffer();
        int bytelength = byteArray.length;
        for (int i = 0; i < bytelength; i++) {
            if (byteArray[i] >= 0 && byteArray[i] < 16) {
                strBuf.append("0");
            }
            strBuf.append(Integer.toHexString(byteArray[i] & 0xFF));
        }
        return strBuf.toString();
    }
}
