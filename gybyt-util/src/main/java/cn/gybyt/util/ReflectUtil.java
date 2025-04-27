package cn.gybyt.util;

import cn.gybyt.constant.ModifierConstant;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 反射工具类
 *
 * @program: utils
 * @classname: ReflectUtil
 * @author: codetiger
 * @create: 2023/3/4 21:16
 **/
@Slf4j
@SuppressWarnings("unchecked")
public class ReflectUtil {

    /**
     * 获取所有字段
     *
     * @param clazz
     * @return
     */
    public static List<Field> getAllFields(Class<?> clazz, ModifierConstant... modifier) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                if (modifier.length > 0) {
                    for (ModifierConstant modifierConstant : modifier) {
                        if (declaredField.getModifiers() == modifierConstant.key()) {
                            fields.add(declaredField);
                        }
                    }
                } else if (modifier.length == 0) {
                    fields.add(declaredField);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    /**
     * 获取所有字段
     *
     * @param t
     * @return
     */
    public static <T> List<Field> getAllFields(T t, ModifierConstant... modifier) {
        Class<?> clazz = ReflectUtil.getClass(t);
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                if (modifier.length > 0) {
                    for (ModifierConstant modifierConstant : modifier) {
                        if (declaredField.getModifiers() == modifierConstant.key()) {
                            fields.add(declaredField);
                        }
                    }
                } else if (modifier.length == 0) {
                    fields.add(declaredField);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    /**
     * 获取所有方法
     *
     * @param clazz
     * @return
     */
    public static List<Method> getAllMethods(Class<?> clazz, Boolean changeModifier, ModifierConstant... modifier) {
        List<Method> methodList = new ArrayList<>();
        while (clazz != null) {
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (changeModifier) {
                    method.setAccessible(true);
                    methodList.add(method);
                }
                else if (modifier.length > 0) {
                    for (ModifierConstant modifierConstant : modifier) {
                        if (method.getModifiers() == modifierConstant.key()) {
                            methodList.add(method);
                        }
                    }
                } else if (modifier.length == 0) {
                    methodList.add(method);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return methodList;
    }

    /**
     * 获取所有字段
     *
     * @param o
     * @return
     */
    public static List<Field> getNotEmptyFields(Object o, ModifierConstant... modifier) {
        if (BaseUtil.isEmpty(o)) {
            return new ArrayList<>();
        }
        List<Field> allFields = getAllFields(o.getClass(), modifier);
        ArrayList<Field> nonNullFields = new ArrayList<>();
        allFields.forEach(field -> {
            field.setAccessible(true);
            try {
                if (BaseUtil.isNotEmpty(field.get(o))) {
                    nonNullFields.add(field);
                }
            } catch (IllegalAccessException e) {}
        });
        return nonNullFields;
    }

    /**
     * 根据字段名称获取字段属性
     * @param o
     * @param name
     * @return
     * @param <T>
     */
    public static <T> T getFieldValueByFieldName(Object o, String name) {
        if (BaseUtil.isEmpty(name)) {
            return null;
        }
        String[] nameArray = name.split("\\.");
        if (nameArray.length == 1) {
            Class<?> aClass = ReflectUtil.getClass(o);
            while (aClass != null) {
                try {
                    Field field = aClass.getDeclaredField(name);
                    field.setAccessible(true);
                    return  (T)field.get(o);
                } catch (NoSuchFieldException | IllegalAccessException ignored) {}
                aClass = aClass.getSuperclass();
            }
            return null;
        }
        for (String fieldName : nameArray) {
            o = getFieldValueByFieldName(o, fieldName);
        }
        return (T) o;
    }

    /**
     * 获取class对象，防止空指针
     * @param o
     * @return
     */
    public static Class<?> getClass(Object o) {
        if (Objects.isNull(o)) {
            return null;
        }
        return o.getClass();
    }

    /**
     * 根据方法名获取结果
     * @param o
     * @param name
     * @param args
     * @return
     * @param <T>
     */
    public static <T> T getMethodResultByMethodName(Object o, String name, Object... args) {
        Class<?> aClass = ReflectUtil.getClass(o);
        Method method;
        while (aClass != null) {
            try {
                Class<?>[] classes = null;
                if (args.length > 0) {
                    classes = new Class[args.length];
                    for (int i = 0; i < args.length; i++) {
                        classes[i] = ReflectUtil.getClass(args[i]);
                    }
                    method = aClass.getDeclaredMethod(name, classes);
                } else {
                    method = aClass.getDeclaredMethod(name);
                }
                method.setAccessible(true);
                return  (T)method.invoke(o, args);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {}
            aClass = aClass.getSuperclass();
        }
        return null;
    }

    /**
     * 根据名称获取字段
     * @param clazz
     * @param name
     * @return
     */
    public static Field getFieldByName(Class<?> clazz, String name) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {}
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    /**
     * 根据名称获取字段
     * @param object
     * @param name
     * @return
     */
    public static Field getFieldByName(Object object, String name) {
        Class aClass = ReflectUtil.getClass(object);
        while (aClass != null) {
            try {
                return aClass.getDeclaredField(name);
            } catch (NoSuchFieldException e) {}
            aClass = aClass.getSuperclass();
        }
        return null;
    }

    /**
     * 新建对象
     * @param o
     * @return
     * @param <T>
     */
    public static <T> T newInstance(Object o) {
        Class<?> aClass = getClass(o);
        try {
            Constructor<?> declaredConstructor = aClass.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            return  (T) declaredConstructor.newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 新建对象
     * @param clazz
     * @return
     * @param <T>
     */
    public static <T> T newInstance(Class<?> clazz) {
        try {
            Constructor<?> declaredConstructor = clazz.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            return  (T) declaredConstructor.newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 是否是相同类型
     * @param className 类名称(全称)
     * @param target 目标类型
     * @return
     */
    public static Boolean isSameType(String className, Class<?> target) {
        if (BaseUtil.isEmpty(className)) {
            return false;
        }
        try {
            Class<?> aClass = Class.forName(className);
            if (aClass.equals(target)) {
                return true;
            }
        } catch (ClassNotFoundException ignored) {}
        return false;
    }

    /**
     * 加载类
     * @param className 类全限定名
     * @return
     * @param <T>
     */
    public static <T> Class<T> loadClass(String className) {
        if (BaseUtil.isEmpty(className)) {
            return null;
        }
        try {
            return (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("加载类 {} 失败", e);
            return null;
        }
    }

    /**
     * 设置字段属性
     * @param o
     * @param filedName
     * @param value
     */
    public static void setFiledValue(Object o, String filedName, Object value) {
        Field field = ReflectUtil.getFieldByName(o, filedName);
        if (field == null) {
            log.error("获取字段失败");
            return;
        }
        field.setAccessible(true);
        try {
            field.set(o, value);
        } catch (IllegalAccessException e) {
            log.error("设置字段{}失败", filedName);
        }
    }
}
