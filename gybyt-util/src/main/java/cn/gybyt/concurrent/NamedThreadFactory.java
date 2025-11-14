package cn.gybyt.concurrent;

import cn.gybyt.util.BaseUtil;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
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
        int index = threadNumber.getAndIncrement();
        Thread t = new Thread(runnable, BaseUtil.format("{}-{}", prefix, index));
        t.setUncaughtExceptionHandler((thread, e) -> log.error(e.getMessage(), e));
        return t;
    }
}
