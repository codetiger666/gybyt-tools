package cn.gybyt.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
     * 编码器
     */
    private final static Base64.Encoder ENCODER = Base64.getEncoder();
    /**
     * 解码器
     */
    private final static Base64.Decoder DECODER = Base64.getDecoder();

    /**
     * @param content 需要编码的字符串
     * @return 编码后的字符串
     * @Author codetiger
     * @Description
     * @Date 22:08 2022/5/15
     * @Param
     **/
    public static String encode(String content) {
        if (BaseUtil.isNotNull(content)) {
            return new String(encodeNoException(content.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        }
        return "";
    }

    /**
     * @param content 需要编码的字符串
     * @param charset 字符编码
     * @return 编码后的字符串
     * @Author codetiger
     * @Description
     * @Date 13:47 2023/08/28
     * @Param
     **/
    public static String encode(String content, Charset charset) {
        if (BaseUtil.isNotNull(content)) {
            return new String(encodeNoException(content.getBytes(charset)), charset);
        }
        return "";
    }

    /**
     * @param content 需要编码的字节数组
     * @return 编码后的字符串
     * @Author codetiger
     * @Description
     * @Date 13:47 2023/08/28
     * @Param
     **/
    public static String encode(byte[] content) {
        if (BaseUtil.isNotNull(content)) {
            return new String(encodeNoException(content), StandardCharsets.UTF_8);
        }
        return "";
    }

    /**
     * @param content 需要编码的字节数组
     * @param charset 字符编码
     * @return 编码后的字符串
     * @Author codetiger
     * @Description
     * @Date 13:47 2023/08/28
     * @Param
     **/
    public static String encode(byte[] content, Charset charset) {
        if (BaseUtil.isNotNull(content)) {
            return new String(encodeNoException(content), charset);
        }
        return "";
    }

    /**
     * @param content 需要编码的字符串
     * @return 编码后的字节数组
     * @Author codetiger
     * @Description
     * @Date 13:47 2023/08/28
     * @Param
     **/
    public static byte[] encodeByte(String content) {
        if (BaseUtil.isNotNull(content)) {
            return encodeNoException(content.getBytes(StandardCharsets.UTF_8));
        }
        return new byte[0];
    }

    /**
     * @param content 需要编码的字符串
     * @param charset 字符编码
     * @return 编码后的字节数组
     * @Author codetiger
     * @Description
     * @Date 13:47 2023/08/28
     * @Param
     **/
    public static byte[] encodeByte(String content, Charset charset) {
        if (BaseUtil.isNotNull(content)) {
            return encodeNoException(content.getBytes(charset));
        }
        return new byte[0];
    }

    /**
     * @param content 需要编码的字节数组
     * @return 编码后的字节数组
     * @Author codetiger
     * @Description
     * @Date 13:47 2023/08/28
     * @Param
     **/
    public static byte[] encodeByte(byte[] content) {
        if (BaseUtil.isNotNull(content)) {
            return encodeNoException(content);
        }
        return new byte[0];
    }

    /**
     * Base64 解码
     * @param content 需要解码的内容
     * @return 编码后的内容
     */
    public static String decode(String content) {
        if (BaseUtil.isNull(content)) {
            return "";
        }
        return new String(decodeNoException(content.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    /**
     * Base64 解码
     * @param content 需要解码的内容
     * @param charset 字符编码
     * @return 编码后的内容
     */
    public static String decode(String content, Charset charset) {
        if (BaseUtil.isNull(content)) {
            return "";
        }
        return new String(decodeNoException(content.getBytes(charset)), charset);
    }

    /**
     * Base64 解码
     * @param content 需要解码的内容
     * @return 编码后的内容
     */
    public static String decode(byte[] content) {
        if (BaseUtil.isNull(content)) {
            return "";
        }
        return new String(decodeNoException(content), StandardCharsets.UTF_8);
    }

    /**
     * Base64 解码
     * @param content 需要解码的内容
     * @param charset 字符编码
     * @return 编码后的内容
     */
    public static String decode(byte[] content, Charset charset) {
        if (BaseUtil.isNull(content)) {
            return "";
        }
        return new String(decodeNoException(content), charset);
    }

    /**
     * Base64 解码
     * @param content 需要解码的内容
     * @return 编码后的内容
     */
    public static byte[] decodeByte(String content) {
        if (BaseUtil.isNull(content)) {
            return new byte[0];
        }
        return decodeNoException(content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64 解码
     * @param content 需要解码的内容
     * @param charset 字符编码
     * @return 编码后的内容
     */
    public static byte[] decodeByte(String content, Charset charset) {
        if (BaseUtil.isNull(content)) {
            return new byte[0];
        }
        return decodeNoException(content.getBytes(charset));
    }

    /**
     * Base64 解码
     * @param content 需要解码的内容
     * @return 编码后的内容
     */
    public static byte[] decodeByte(byte[] content) {
        if (BaseUtil.isNull(content)) {
            return new byte[0];
        }
        return decodeNoException(content);
    }

    /**
     * 编码
     * @return
     */
    private static byte[] encodeNoException(byte[] data) {
        try {
            return ENCODER.encode(data);
        } catch (Exception e) {
            return data;
        }
    }

    /**
     * 解码
     * @return
     */
    private static byte[] decodeNoException(byte[] data) {
        try {
            return DECODER.decode(data);
        } catch (Exception e) {
            return data;
        }
    }
}
