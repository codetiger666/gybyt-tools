package cn.gybyt.util;

import cn.gybyt.io.ByteArrayOutputStream;
import cn.gybyt.tools.PatternPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;

/**
 * 文件操作工具类
 *
 * @program: utils
 * @classname: FileUtil
 * @author: Codetiger
 * @create: 2022/5/14 17:58
 **/

public class FileUtil {

    private final static Logger log = LoggerFactory.getLogger("FileUtil");

    /**
     * @param path 文件路径
     * @return base64编码文件字符串
     * @Author codetiger
     * @Date 18:42 2022/5/14
     * @Param
     **/
    public static String fileToBase64(String path) {
        String base64 = null;
        File file = new File(path);
        try (InputStream in = new FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()];
            in.read(bytes);
            base64 = Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            log.error("打开文件出错");
            LoggerUtil.handleException(log, e);
        }
        return base64;
    }

    /**
     * @param base64 base64编码文件字符串
     * @param file   文件
     * @return
     * @Author codetiger
     * @Date 18:43 2022/5/14
     * @Param
     **/
    public synchronized static void base64ToFile(String base64, File file) {
        try (OutputStream outputStream = new FileOutputStream(file)) {
            byte[] bytes = Base64.getDecoder().decode(base64);
            outputStream.write(bytes);
        } catch (Exception e) {
            log.error("文件写入失败");
            LoggerUtil.handleException(log, e);
        }
    }

    /**
     * @param path 文件路径
     * @return 文件列表
     * @Author codetiger
     * @Description
     * @Date 9:26 2022/7/19
     * @Param
     **/
    public static List<String> listDirFiles(String path) {
        ArrayList<String> files = new ArrayList<>();
        File file = new File(path);
        if (file.isDirectory()) {
            File[] tempList = file.listFiles();
            if (BaseUtil.isEmpty(tempList)) {
                return new ArrayList<>();
            }
            for (File value : tempList) {
                //如果文件存在
                if (value.isFile()) {
                    files.add(value.getName());
                }
            }
            return files;
        } else {
            throw new BaseException("文件夹不存在");
        }
    }

    /**
     * 读取文件内容为字符串
     *
     * @param path
     * @return
     */
    public static String readString(String path) {
        if (BaseUtil.isEmpty(path)) {
            return "";
        }
        InputStream inputStream = getFileInputStream(path);
        if (BaseUtil.isEmpty(inputStream)) {
            return "";
        }
        return new String(readInputStream(inputStream), StandardCharsets.UTF_8);
    }

    /**
     * 读取文件内容为字符串
     *
     * @param path
     * @return
     */
    public static String readString(String path, Charset charset) {
        if (BaseUtil.isEmpty(path)) {
            return "";
        }
        InputStream inputStream = getFileInputStream(path);
        if (BaseUtil.isEmpty(inputStream)) {
            return "";
        }
        return new String(readInputStream(inputStream), charset);
    }

    /**
     * 获取文件输入流
     *
     * @param path
     * @return
     */
    public static InputStream getFileInputStream(String path) {
        try {
            if (BaseUtil.isEmpty(path)) {
                return null;
            }
            URL url;
            path = path.replaceAll("^classpath:", "");
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            if (inputStream != null) {
                return inputStream;
            }
            url = Thread.currentThread().getContextClassLoader().getResource(path);
            path = BaseUtil.isNull(url) ? path : url.getPath();
            path = path.replaceAll("^file:/", "");
            Matcher matcher = PatternPool.getPattern("(.*?\\.jar)!?(.+)").matcher(path);
            if (matcher.find()) {
                if (matcher.groupCount() < 2) {
                    log.error("匹配文件名失败");
                    return null;
                }
                JarFile jarFile = new JarFile(matcher.group(1));
                ZipEntry entry = jarFile.getEntry(matcher.group(2).replaceAll("\\\\", "/").replaceAll("^/", ""));
                if (BaseUtil.isNull(entry)) {
                    return null;
                }
                return jarFile.getInputStream(entry);
            }
            return Files.newInputStream(Paths.get(path));
        } catch (IOException e) {
            log.error("读取文件失败", e);
            return null;
        }
    }

    /**
     * 输入流中读取数据
     *
     * @param inputStream
     * @return
     */
    public static byte[] readInputStream(InputStream inputStream) {
        if (BaseUtil.isNull(inputStream)) {
            return new byte[0];
        }

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            int maxBufferSize = 1024;
            byte[] bufferByte = new byte[maxBufferSize];
            int len;
            while ((len = inputStream.read(bufferByte, 0, maxBufferSize)) != -1) {
                byteArrayOutputStream.write(bufferByte, 0, len);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }
}
