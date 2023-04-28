package cn.gybyt.util;

import org.slf4j.Logger;

import java.util.regex.Pattern;

/**
 * 日志打印工具类
 *
 * @program: utils
 * @classname: Logger
 * @author: Codetiger
 * @create: 2022/5/14 16:31
 **/

public class LoggerUtil {
    public static void handleException(Logger log, Exception e) {
        // 获取异常堆栈信息
        StackTraceElement[] stackTrace = e.getStackTrace();
        // 记录异常的层数
        int post = 0;
        // 匹配需要的信息
        String pattern = ".*\\.java:\\d*\\)";
        // 匹配跳出循环条件
        String patternEnd = ".*java\\.base/jdk\\.internal.*";
        for (StackTraceElement stackTraceElement : stackTrace) {
            // 判断是否符合需要的条件
            boolean matches = Pattern.matches(pattern, stackTraceElement.toString());
            if (matches) {
                post++;
            }
            // 判断何时跳出循环
            boolean matchesEnd = Pattern.matches(patternEnd, stackTraceElement.toString());
            if (matchesEnd) {
                break;
            }
        }
        // 对需要输出的堆栈信息进行打印
        for (int i = 0; i < post; i++) {
            log.error(stackTrace[i].toString());
        }
    }


}

