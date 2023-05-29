package cn.gybyt.util;

import java.util.Base64;

/**
 * Base64工具类
 *
 * @program: utils
 * @classname: BaseUtil
 * @author: Codetiger
 * @create: 2022/5/15 21:48
 **/
public class Base64Util {

    /**
     * @param content 需要编码的字符串
     * @return 编码后的字符串
     * @Author codetiger
     * @Description //TODO
     * @Date 22:08 2022/5/15
     * @Param
     **/
    public static String encodeBase64Str(String content) {
        Base64.Encoder base64 = Base64.getEncoder();
        return base64.encodeToString(content.getBytes());
    }
    /**
     * @param content 需要编码的字符串
     * @return 编码后的字节数组
     * @Author codetiger
     * @Description //TODO
     * @Date 22:08 2022/5/15
     * @Param
     **/
    public static byte[] encodeBase64Byte(String content) {
        Base64.Encoder base64 = Base64.getEncoder();
        return base64.encode(content.getBytes());
    }
    /**
     * @param content 需要编码的字节数组
     * @return 编码后的字符串
     * @Author codetiger
     * @Description //TODO
     * @Date 22:08 2022/5/15
     * @Param
     **/
    public static String encodeBase64Str(byte[] content) {
        Base64.Encoder base64 = Base64.getEncoder();
        return base64.encodeToString(content);
    }
    /**
     * @param content 需要编码的字节数组
     * @return 编码后的字符串
     * @Author codetiger
     * @Description //TODO
     * @Date 22:08 2022/5/15
     * @Param
     **/
    public static byte[] encodeBase64Byte(byte[] content) {
        Base64.Encoder base64 = Base64.getEncoder();
        return base64.encode(content);
    }
    /**
     * @param base64 需要解码的base64字符串
     * @return 解码后的字符串
     * @Author codetiger
     * @Description //TODO
     * @Date 21:54 2022/5/15
     * @Param
     **/
    public static String base64DecodeStr(String base64) {
        Base64.Decoder decoder = Base64.getDecoder();
        return new String(decoder.decode(base64));
    }
    /**
     * @param base64 需要解码的base64字符串
     * @return 解码后的字节数组
     * @Author codetiger
     * @Description //TODO
     * @Date 21:54 2022/5/15
     * @Param
     **/
    public static byte[] base64DecodeByte(String base64) {
        Base64.Decoder decoder = Base64.getDecoder();
        return decoder.decode(base64);
    }
}
