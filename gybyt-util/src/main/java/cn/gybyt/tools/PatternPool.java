package cn.gybyt.tools;

import cn.gybyt.util.BaseUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 *
 * 正则表达式池
 *
 * @program: gybyt-tools
 * @classname: PatternPool
 * @author: codetiger
 * @create: 2023/8/16 19:34
 **/
public class PatternPool {

    private final static Map<String, Pattern> POOl = new ConcurrentHashMap<>();

    /**
     * 获取匹配对象
     * @param regex 正则表达式
     * @return
     */
    public static Pattern getPattern(String regex) {
        if (BaseUtil.isEmpty(regex)) {
            return null;
        }
        return POOl.computeIfAbsent(regex, k -> Pattern.compile(regex));
    }

    /**
     * 获取所有Pattern对象
     * @return
     */
    public static List<Pattern> getPatternList() {
        return (List<Pattern>) POOl.values();
    }

    /**
     * 删除Pattern对象
     * @param regex 正则表达式
     * @return
     */
    public static Pattern remove(String regex) {
        if (BaseUtil.isEmpty(regex)) {
            return null;
        }
        return POOl.remove(regex);
    }

}
