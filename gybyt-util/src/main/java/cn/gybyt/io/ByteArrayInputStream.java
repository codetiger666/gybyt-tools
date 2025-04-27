package cn.gybyt.io;

import java.io.IOException;

/**
 * 字节数组输入流
 *
 * @program: gybyt-tools
 * @classname: ByteArrayInputStream
 * @author: codetiger
 * @create: 2024/1/2 19:00
 **/
public class ByteArrayInputStream extends java.io.ByteArrayInputStream {

    public ByteArrayInputStream(byte[] buf, int offset, int length) {
        super(buf, offset, length);
    }

    @Override
    public synchronized void close() throws IOException {
        super.close();
        this.buf = new byte[0];
    }
}
