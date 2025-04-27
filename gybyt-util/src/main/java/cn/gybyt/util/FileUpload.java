package cn.gybyt.util;

import java.io.InputStream;

/**
 * 文件上传
 *
 * @program: gybyt-tools
 * @classname: FileUpload
 * @author: codetiger
 * @create: 2023/12/28 18:50
 **/
public interface FileUpload {

    /**
     * 上传文件
     * @param inputStream
     * @param fileName
     * @return
     */
    FileInfo putFile(InputStream inputStream, String filePath , String fileName);

    /**
     * 获取文件
     * @param filePath 文件路径
     * @param fileName 文件名
     * @return 输入流
     */
    InputStream getFile(String filePath, String fileName);

    class FileInfo {
        /**
         * 链接
         */
        private String url;
        /**
         * 文件名称
         */
        private String name;
        /**
         * 真实名称
         */
        private String rawName;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRawName() {
            return rawName;
        }

        public void setRawName(String rawName) {
            this.rawName = rawName;
        }

        @Override
        public String toString() {
            return "FileInfo{" +
                    "url='" + url + '\'' +
                    ", name='" + name + '\'' +
                    ", rawName='" + rawName + '\'' +
                    '}';
        }
    }

}
