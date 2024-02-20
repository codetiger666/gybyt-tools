package cn.gybyt.schedule;

import cn.gybyt.concurrent.NamedThreadFactory;
import cn.gybyt.util.BaseException;
import cn.gybyt.util.BaseUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * 定时任务管理器
 *
 * @program: gybyt-tools
 * @classname: TaskManage
 * @author: codetiger
 * @create: 2024/2/5 20:32
 **/
@Slf4j
public class TaskManage {
    /**
     * 任务集合
     */
    private static final Map<String, TaskExecute> TASKS = new ConcurrentHashMap<>();
    /**
     * 执行线程池
     */
    private ScheduledExecutorService executorPool = new ScheduledThreadPoolExecutor(5, new NamedThreadFactory(
            "定时任务线程池"));

    public void setExecutorPool(ScheduledExecutorService executorPool) {
        this.executorPool = executorPool;
    }

    /**
     * 添加任务
     *
     * @param taskExecute 任务
     * @return
     */
    public Boolean addTask(TaskExecute taskExecute) {
        if (taskExecute == null) {
            throw new BaseException("任务对象不能为空");
        }
        TaskManage.checkCron(taskExecute.getCron());
        if (TASKS.containsKey(taskExecute.getTaskName())) {
            throw new BaseException(BaseUtil.format("当前任务名称已存在：{}", taskExecute.getTaskName()));
        }
        taskExecute.execute(this.executorPool);
        TASKS.put(taskExecute.getTaskName(), taskExecute);
        return true;
    }

    /**
     * 更新任务
     *
     * @param taskName 任务名称
     * @param cron     cron表达式
     */
    public Boolean updateTask(String taskName, String cron) {
        TaskManage.checkCron(cron);
        if (BaseUtil.isEmpty(taskName)) {
            throw new BaseException("任务名称不能为空");
        }
        TaskExecute taskExecute = TASKS.get(taskName);
        if (taskExecute == null) {
            throw new BaseException(BaseUtil.format("{}, 任务不存在", taskName));
        }
        TaskExecute newTask = TaskManage.genTaskExecute(taskName, cron, taskExecute.getClass()
                                                                                   .getName());
        newTask.execute(this.executorPool);
        taskExecute.setCancel(true);
        TASKS.put(taskName, newTask);
        return true;
    }

    /**
     * 任务列表
     *
     * @param taskName 任务列表
     * @return
     */
    public static List<Map<String, String>> getTaskInfo(String taskName) {
        List<Map<String, String>> dataList = new ArrayList<>();
        TASKS.forEach((k, v) -> {
            if (BaseUtil.isNotEmpty(taskName) && taskName.equals(k)) {
                return;
            }
            Map<String, String> dataMap = new HashMap<>(3);
            dataMap.put("taskName", v.getTaskName());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dataMap.put("nextDate", dateFormat.format(v.getNextDate()));
            dataMap.put("class", v.getClass()
                                  .toString()
                                  .replace("class: ", ""));
            dataList.add(dataMap);
        });
        return dataList;
    }

    /**
     * 删除任务
     *
     * @param taskName 任务名称
     * @return
     */
    public static Boolean removeTask(String taskName) {
        if (BaseUtil.isEmpty(taskName)) {
            throw new BaseException("任务名称不能为空");
        }
        TaskExecute taskExecute = TASKS.get(taskName);
        if (taskExecute == null) {
            throw new BaseException(BaseUtil.format("{}, 任务不存在", taskName));
        }
        taskExecute.setCancel(true);
        TASKS.remove(taskName);
        return true;
    }

    /**
     * 生成任务执行对象
     *
     * @param taskName  任务名称
     * @param cron      cron表达式
     * @param className 类全限定名
     * @return 执行对象实例
     */
    public static TaskExecute genTaskExecute(String taskName, String cron, String className) {
        try {
            Class<?> taskExecuteClass = Class.forName(className);
            Constructor<?> constructor = taskExecuteClass
                    .getConstructor(String.class, String.class);
            return (TaskExecute) constructor.newInstance(cron, taskName);
        } catch (Exception e) {
            log.error("生成任务执行对象失败", e);
            throw new BaseException("生成任务执行对象失败");
        }
    }

    /**
     * 校验cron表达式
     *
     * @param cron cron表达式
     */
    public static void checkCron(String cron) {
        if (BaseUtil.isEmpty(cron)) {
            throw new BaseException("cron不能为空");
        }
        try {
            new CronExpression(cron);
        } catch (Exception e) {
            log.error("cron表达式解析失败, {}", e.getMessage());
            throw new BaseException(e.getMessage());
        }
    }
}
