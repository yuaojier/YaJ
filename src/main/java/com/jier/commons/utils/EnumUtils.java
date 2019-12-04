package com.jier.commons.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author yuaoj
 * @date 2019/11/25 17:13
 */
public enum EnumUtils {
    /**
     * 单例
     */
    INSTANCE;

    public static Boolean instance(Class<? extends Enum<?>> clazz, String s) {
        final Enum<?>[] enumConstants = clazz.getEnumConstants ( );
        if ( StringUtils.isBlank ( s ) || null == enumConstants ) {
            return false;
        }
        for (Enum<?> e : enumConstants) {
            if ( StringUtils.equals ( e.name ( ), s ) ) {
                return true;
            }
        }
        return false;
    }
}
