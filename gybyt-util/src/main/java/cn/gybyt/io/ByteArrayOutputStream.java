package cn.gybyt.io;

import java.io.IOException;

/**
 * 字节数组输出流
 *
 * @program: gybyt-tools
 * @classname: ByteArrayOutputStream
 * @author: codetiger
 * @create: 2024/1/2 19:01
 **/
public class ByteArrayOutputStream extends java.io.ByteArrayOutputStream {

    @Override
    public synchronized void close() throws IOException {
        super.close();
        this.buf = new byte[0];
    }
}
