package cn.gybyt.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 生成带泛型的Class
 *
 * @program: utils
 * @classname: TypeUtil
 * @author: codetiger
 * @create: 2022/11/10 21:31
 **/
@SuppressWarnings("unchecked")
public abstract class TypeUtil<T> {

    private final Type type;

    protected TypeUtil(){
        Class<?> parameterizedTypeReferenceSubclass = findParameterizedTypeReferenceSubclass(ReflectUtil.getClass(this));
        Type type = parameterizedTypeReferenceSubclass.getGenericSuperclass();
        ParameterizedType parameterizedType = (ParameterizedType)type;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        this.type = actualTypeArguments[0];
    }

    public Type getType(){
        return this.type;
    }

    public Class<T> getTypeClass(){
        return (Class<T>) this.type;
    }

    public String getClassName(){
        return this.type.toString();
    }

    private static Class<?> findParameterizedTypeReferenceSubclass(Class<?> child) {
        Class<?> parent = child.getSuperclass();
        if (Object.class == parent) {
            throw new IllegalStateException("Expected ParameterizedTypeReference superclass");
        } else {
            return TypeUtil.class == parent ? child : findParameterizedTypeReferenceSubclass(parent);
        }
    }
}
