package com.xqq.myradar.radar.Utils;



import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;

public class HexUtil {

    /**
     * 将十六进制字符串转换字节数组
     * @param str
     * @return 字节数组
     */
    public static byte[] hexStringToBytes(String str) {
        if(str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }

    /**
     * 字节数组转char
     * @param b
     * @return
     */
    public static char byteToChar(byte[] b) {
        char c = (char) (((b[0] & 0xFF) << 8) | (b[1] & 0xFF));
        return c;
    }


    /**
     * 将16进制字符串转换为byte数组
     * @param hexItr 16进制字符串
     * @return
     */
    public static byte[] hexStr2ByteArr(String hexItr) throws DecoderException {
        return Hex.decodeHex(hexItr);
    }

    /**
     * 将普通字符串转换为16进制字符串
     * @param str 普通字符串
     * @param lowerCase 转换后的字母为是否为小写  可不传默认为true
     * @param charset 编码格式  可不传默认为Charset.defaultCharset()
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String str2HexStr(String str,boolean lowerCase,String charset) throws UnsupportedEncodingException {
        return Hex.encodeHexString(str.getBytes(charset),lowerCase);
    }

    /**
     * 将16进制字符串转换为byte数组
     * @param hexItr 16进制字符串
     * @return
     */
    public static byte[] hexStr2Arr(String hexItr) throws DecoderException {
        return Hex.decodeHex(hexItr);
    }


}
