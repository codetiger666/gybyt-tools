package cn.gybyt.util;

import java.lang.reflect.Field;
import java.util.*;

/**
 * bean工具类
 *
 * @program: utils
 * @classname: BeanUtil
 * @author: codetiger
 * @create: 2023/3/14 18:33
 **/
public class BeanUtil {

    /**
     * 复制对象属性
     * @param source
     * @param target
     * @param cover
     * @return
     * @param <T>
     * @param <R>
     */
    public static <T, R> void copy(T source, R target, Boolean cover) {
        Map<String, Field> targetFieldMap = BaseUtil.listToMap(ReflectUtil.getAllFields(ReflectUtil.getClass(target)), Field::getName);
        Map<String, Field> sourceFieldMap = BaseUtil.listToMap(ReflectUtil.getAllFields(ReflectUtil.getClass(source)), Field::getName);
        Set<String> targetFiledKeySet = targetFieldMap.keySet();
        Set<String> sourceFiledKeySet = sourceFieldMap.keySet();
        HashSet<String> retainSet = new HashSet<>();
        retainSet.addAll(targetFiledKeySet);
        retainSet.retainAll(sourceFiledKeySet);
        Iterator<String> iterator = retainSet.iterator();
        while (iterator.hasNext()) {
            String fieldName = iterator.next();
            try {
                Field targetFiled = ReflectUtil.getFieldByName(target, fieldName);
                Field sourceFiled = ReflectUtil.getFieldByName(source, fieldName);
                targetFiled.setAccessible(true);
                sourceFiled.setAccessible(true);
                if (cover || BaseUtil.isEmpty(targetFiled.get(target))) {
                    targetFiled.set(target, sourceFiled.get(source));
                }
            } catch (IllegalAccessException e) {}
        }
    }

    /**
     * 复制对象
     *
     * @param source
     * @param target
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> R copy(T source, Class<R> target) {
        R result = ReflectUtil.newInstance(target);
        Map<String, Field> targetFieldMap = BaseUtil.listToMap(ReflectUtil.getAllFields(target), Field::getName);
        Map<String, Field> sourceFieldMap = BaseUtil.listToMap(ReflectUtil.getAllFields(source), Field::getName);
        Set<String> targetFiledKeySet = targetFieldMap.keySet();
        Set<String> sourceFiledKeySet = sourceFieldMap.keySet();
        HashSet<String> retainSet = new HashSet<>();
        retainSet.addAll(targetFiledKeySet);
        retainSet.retainAll(sourceFiledKeySet);
        Iterator<String> iterator = retainSet.iterator();
        while (iterator.hasNext()) {
            String fieldName = iterator.next();
            try {
                Field targetFiled = ReflectUtil.getFieldByName(result, fieldName);
                Field sourceFiled = ReflectUtil.getFieldByName(source, fieldName);
                targetFiled.setAccessible(true);
                sourceFiled.setAccessible(true);
                targetFiled.set(result, sourceFiled.get(source));
            } catch (IllegalAccessException e) {}
        }
        return result;
    }

    /**
     * 复制对象属性(默认不覆盖原值)
     * @param source
     * @param target
     * @return
     * @param <T>
     * @param <R>
     */
    public static <T, R> void copy(T source, R target) {
       copy(source, target, false);
    }

}
