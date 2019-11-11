package com.jier.commons.utils;

import com.google.common.primitives.Primitives;
import sun.misc.Unsafe;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 *
 * 功能描述: 深度拷贝  基于魔法类 Unsafe 不确定是否会对堆外内存发生影响
 *
 * @author yuaoj
 * @date 2019/11/5 14:56
 */
public final class CopyUtils {

    private CopyUtils() {
    }


    public static <T> T copy(T src) throws IllegalAccessException, NoSuchFieldException, InstantiationException {
        return doCopy(src, new IdentityHashMap<Object, Object> ());
    }


    private static <T> T doCopy(T src, Map<Object, Object> visited) throws IllegalAccessException, NoSuchFieldException, InstantiationException {
        if (src == null) {
            return null;
        }
        if (src.getClass().isAssignableFrom(String.class)
                || src.getClass().isEnum()
                || src.getClass() == Class.class) {
            return src;
        }
        if (visited.containsKey(src)) {
            if ( src.getClass ().isLocalClass () ){
                return src;
            }
            return (T) visited.get(src
            );
        }
        if (src.getClass().isArray()) {
            return copyArray(src, visited);
        }
        return copyObject(src, visited);
    }


    private static Unsafe getUnsafe() throws NoSuchFieldException, IllegalAccessException {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (Unsafe) f.get(null);
    }

    private static <T> T copyArray(T src, Map<Object, Object> visited) throws IllegalAccessException, NoSuchFieldException, InstantiationException {
        int length = Array.getLength(src);
        Object result =
                Array.newInstance(
                        src.getClass().getComponentType(),
                        Array.getLength(src));
        visited.put(src, result);
        for (int i = 0; i < length; i++) {
            Array.set(result, i, doCopy(Array.get(src, i), visited));
        }
        return (T) result;
    }

    private static boolean hasModifier(Field field, int modifier) {
        return (field.getModifiers() & modifier) != 0;
    }


    private static <T> T copyObject(T src, Map<Object, Object> visited) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        Object result = getUnsafe().allocateInstance(src.getClass());
        visited.put(src, result);
        Class<?> clazz = src.getClass();
        do {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                // if the field is static or final we won't do anything with it
                if (hasModifier(field, Modifier.STATIC) || hasModifier(field, Modifier.FINAL)) {
                    continue;
                }
                field.setAccessible(true);
                // if it's primitive, we just set it's value to our new instance
                // otherwise we recursively copy it's value
                if (Primitives.unwrap(field.getType()).isPrimitive()) {
                    field.set(result, field.get(src));
                } else {
                    field.set(result, doCopy(field.get(src), visited));
                }
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null);
        return (T) result;
    }

    /**
     * Deep-copy the given object.
     *
     * @param source the original object.
     * @return the deep-copied object instance.
     */
    public static <T> T deepCopy(T source) {
        try {
            return CopyUtils.copy(source);
        } catch (Exception e) {
            return null;
        }
    }
}
