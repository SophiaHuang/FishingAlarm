package com.fusi.fishingalarm.utils;

/**
 * Created by user90 on 2016/6/17.
 */
public class StringUtils {
    //    这个可以把16进制接收上来的数据，以16进制形式在String中表现
    public static String HexShowAsString(int i) {
        String hex = Integer.toHexString(i & 0xFF);  //这个可以把16进制接收上来的数据，以16进制形式在String中表现
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        return hex;
    }

    /**
     * 转换IP
     *
     * @param ipAddress
     * @return
     */
    public static String ipIntToString(int ipAddress) {
        return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
    }

    public static String Byte2HexString(byte b) {
        String hex = Integer.toHexString(b & 0xff);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        return hex;
    }
}
