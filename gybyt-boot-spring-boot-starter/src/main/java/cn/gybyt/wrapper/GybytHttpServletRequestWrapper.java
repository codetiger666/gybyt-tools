package cn.gybyt.wrapper;

import cn.gybyt.util.FileUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * request对象包装
 *
 * @program: utils
 * @classname: GybytHttpServletRequestWrapper
 * @author: codetiger
 * @create: 2023/1/17 19:34
 **/
public class GybytHttpServletRequestWrapper extends HttpServletRequestWrapper {

    /**
     * request对象
     */
    private final HttpServletRequest request;
    /**
     * 缓存请求报文，以支持多次读取
     */
    private byte[] body;

    public GybytHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.request = request;
    }

    /**
     *
     * @return
     * @throws IOException
     */
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    /**
     *
     * @return
     * @throws IOException
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        // 不处理无请求体请求
        if (super.getHeader(HttpHeaders.CONTENT_TYPE) == null) {
            return super.getInputStream();
        }
        // 不处理文件类型请求
        if (super.getHeader(HttpHeaders.CONTENT_TYPE).startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            return super.getInputStream();
        }
        if (this.body == null) {
            // 请求体为空时不再执行读操作
            if (this.request.getContentLength() != -1) {
                this.body = FileUtil.readInputStream(super.getInputStream());
            } else {
                this.body = new byte[0];
            }
        }

        // 新建字节输入流对象
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.body);

        // 创建新的输入流
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }
            @Override
            public boolean isReady() {
                return false;
            }
            @Override
            public void setReadListener(ReadListener listener) {

            }
            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };

    }

}
