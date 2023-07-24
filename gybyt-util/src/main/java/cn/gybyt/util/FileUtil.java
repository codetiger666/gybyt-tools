package cn.gybyt.util;

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
import java.util.regex.Pattern;
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
     * jar 文件正则检验器
     */
    private final static Pattern JAR_FILE_PATTERN = Pattern.compile("(.*?\\.jar)!?(.+)");

    /**
     * @param path 文件路径
     * @return base64编码文件字符串
     * @Author codetiger
     * @Date 18:42 2022/5/14
     * @Param
     **/
    public static String fileToBase64(String path) {
        String base64 = null;
        InputStream in = null;
        try {
            File file = new File(path);
            in = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            in.read(bytes);
            base64 = Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            log.error("打开文件出错");
            LoggerUtil.handleException(log, e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("文件关闭出错");
                    LoggerUtil.handleException(log, e);
                }
            }
        }
        return base64;
    }

    /**
     * @param base64   base64编码文件字符串
     * @param path     文件目录
     * @param fileName 文件名称
     * @return
     * @Author codetiger
     * @Date 18:43 2022/5/14
     * @Param
     **/
    public synchronized static void base64ToFile(String base64, String path, String fileName) {
        File file = null;
        //创建文件目录
        File dir = new File(path);
        if (!dir.exists() && !dir.isDirectory()) {
            dir.mkdirs();
        }
        BufferedOutputStream bos = null;
        java.io.FileOutputStream fos = null;
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            file = new File(path + "\\" + fileName);
            fos = new java.io.FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            fos.getChannel().tryLock();
            bos.write(bytes);
        } catch (Exception e) {
            log.error("文件写入失败");
            LoggerUtil.handleException(log, e);
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    log.error("文件关闭失败");
                    LoggerUtil.handleException(log, e);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    log.error("文件关闭失败");
                    LoggerUtil.handleException(log, e);
                }
            }
        }
    }

    /**
     * @Author codetiger
     * @Description //TODO
     * @Date 9:26 2022/7/19
     * @Param
     * @param path 文件路径
     * @return 文件列表
     **/
    public static List<String> listDirFiles(String path) {
        ArrayList<String> files = new ArrayList<>();
        File file = new File(path);
        if (file.isDirectory()){
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
        }else{
            throw new BaseException("文件夹不存在");
        }
    }

    /**
     * 读取文件内容为字符串
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
     * @param path
     * @return
     */
    public static InputStream getFileInputStream(String path) {
        try {
            if (BaseUtil.isEmpty(path)) {
                return null;
            }
            URL url;
            if (path.startsWith("classpath:")) {
                path = path.replaceAll("^classpath:", "");
                url = Thread.currentThread().getContextClassLoader().getResource(path);
                path = url.getPath();
            }
            path = path.replaceAll("^file:", "");
            Matcher matcher = JAR_FILE_PATTERN.matcher(path);
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
     * @param inputStream
     * @return
     */
    public static byte[] readInputStream(InputStream inputStream) {
        if (BaseUtil.isNull(inputStream)) {
            return new byte[0];
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            int maxBufferSize = 1024;
            byte[] bufferByte = new byte[maxBufferSize];
            int len;
            while ((len = inputStream.read(bufferByte, 0, maxBufferSize)) != -1) {
                byteArrayOutputStream.write(bufferByte, 0, len);
            }
        } catch (IOException e) {
            return new byte[0];
        }
        return byteArrayOutputStream.toByteArray();
    }
}
