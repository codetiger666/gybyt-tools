package cn.gybyt.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;

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
    public ArrayList<String> listDirFiles(String path) throws Exception {
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
            throw new Exception("文件夹不存在");
        }
    }
}
