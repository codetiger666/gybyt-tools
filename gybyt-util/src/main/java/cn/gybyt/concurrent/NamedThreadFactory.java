package cn.gybyt.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 具名线程工厂
 *
 * @program: gybyt-tools
 * @classname: NamedThreadFactory
 * @author: codetiger
 * @create: 2024/1/1 17:43
 **/
public class NamedThreadFactory implements ThreadFactory {

    /**
     * 命名前缀
     */
    private final String prefix;
    /**
     * 线程组
     */
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    public NamedThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        return new Thread(runnable, String.format("%s-%s", prefix, threadNumber.getAndIncrement()));
    }
}
