package cn.gybyt.schedule;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务抽象类
 *
 * @program: gybyt-tools
 * @classname: TaskExecute
 * @author: codetiger
 * @create: 2024/2/5 20:05
 **/
@Slf4j
public abstract class TaskExecute implements Runnable {
    /**
     * cron 表达式
     */
    private final String cron;
    /**
     * 任务执行时间
     */
    private Date executeDate;
    /**
     * 任务下次执行日期
     */
    private Date nextDate;
    /**
     * 任务是否取消
     */
    private boolean cancel = false;
    /**
     * 执行线程池
     */
    private ScheduledExecutorService executorPool;
    /**
     * 锁
     */
    private final Object lock = new Object();
    /**
     * 任务名称
     */
    private final String taskName;

    public TaskExecute(String cron, String taskName) {
        this.cron = cron;
        this.taskName = taskName;
    }

    /**
     * 执行
     * @param executorPool 线程池
     * @return 任务
     */
    public TaskExecute execute(ScheduledExecutorService executorPool) {
        this.executorPool = executorPool;
        CronExpression cronExpression = new CronExpression(this.cron);
        if (this.executeDate == null) {
            this.executeDate = new Date();
        }
        this.nextDate = cronExpression.next(this.executeDate);
        long delay = this.nextDate.getTime() - System.currentTimeMillis();
        if (this.cancel) {
            return null;
        }
        this.executeDate = this.nextDate;
        this.executorPool.schedule(this, delay, TimeUnit.MILLISECONDS);
        return this;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getCron() {
        return cron;
    }

    public Date getNextDate() {
        return nextDate;
    }

    @Override
    public void run() {
        synchronized (this.lock) {
            try {
                this.runTask();
            } catch (Exception e) {
                log.error("任务执行失败", e);
                throw e;
            } finally {
                this.execute(this.executorPool);
            }
        }
    }

    /**
     * 实际运行方法
     */
    protected abstract void runTask();
}
