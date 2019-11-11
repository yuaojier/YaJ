package com.jier.commons.utils;

import com.sun.tools.javac.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author: yuaoj
 * @date: 2019/11/11 10:42
 * @description:
 */
public class BigDecimalUtils {
    /**
     * 加法
     * @param value1
     * @param value2
     * @param <T>
     * @return
     */
    public static <T extends Number>BigDecimal add(T value1,T value2){
        return BigDecimalUtils.add ( value1,value2,2 );
    }

    /**
     * 指定位数
     * @param value1
     * @param value2
     * @param scale
     * @param <T>
     * @return
     */
    public static <T extends Number>BigDecimal add(T value1,T value2 ,int scale){
        return BigDecimalUtils.add ( value1,value2,scale, RoundingMode.HALF_UP );
    }

    /**
     * 四舍五入
     * @param value1
     * @param value2
     * @param scale
     * @param roundingMode
     * @param <T>
     * @return
     */
    public static <T extends Number> BigDecimal add(T value1, T value2, int scale, RoundingMode roundingMode) {
        BigDecimal bigDecimal = new BigDecimal ( value1.doubleValue ( ) );
        BigDecimal bigDecimal1 = new BigDecimal ( value2.doubleValue ( ) );
        return bigDecimal.add ( bigDecimal1 ).setScale ( scale, roundingMode);
    }

    /**
     * 减法
     * @param value1
     * @param value2
     * @param <T>
     * @return
     */
    public static <T extends Number>BigDecimal sub(T value1,T value2){
        return BigDecimalUtils.sub ( value1,value2,2 );
    }

    /**
     * 指定位数
     * @param value1
     * @param value2
     * @param scale
     * @param <T>
     * @return
     */
    public static <T extends Number>BigDecimal sub(T value1,T value2 ,int scale){
        return BigDecimalUtils.sub ( value1,value2,scale, RoundingMode.HALF_UP );
    }

    /**
     * 指定四舍五入减法
     * @param value1
     * @param value2
     * @param scale
     * @param roundingMode
     * @param <T>
     * @return
     */
    public static <T extends Number> BigDecimal sub(T value1 ,T value2 ,int scale ,RoundingMode roundingMode){
        BigDecimal bigDecimal = new BigDecimal ( value1.doubleValue ( ) );
        BigDecimal bigDecimal1 = new BigDecimal ( value2.doubleValue ( ) );
        return bigDecimal.subtract ( bigDecimal1 ).setScale ( scale, roundingMode);
    }

    /**
     * 乘法
     * @param value1
     * @param value2
     * @param <T>
     * @return
     */
    public static <T extends Number>BigDecimal mul(T value1,T value2){
        return BigDecimalUtils.mul ( value1,value2,2 );
    }

    /**
     * 指定位数
     * @param value1
     * @param value2
     * @param scale
     * @param <T>
     * @return
     */
    public static <T extends Number>BigDecimal mul(T value1,T value2 ,int scale){
        return BigDecimalUtils.mul ( value1,value2,scale, RoundingMode.HALF_UP );
    }

    /**
     * 四舍五入
     * @param value1
     * @param value2
     * @param scale
     * @param roundingMode
     * @param <T>
     * @return
     */

    public static <T extends Number> BigDecimal mul(T value1 ,T value2 ,int scale ,RoundingMode roundingMode){
        BigDecimal bigDecimal = new BigDecimal ( value1.doubleValue ( ) );
        BigDecimal bigDecimal1 = new BigDecimal ( value2.doubleValue ( ) );
        return bigDecimal.multiply ( bigDecimal1 ).setScale ( scale, roundingMode);
    }

    /**
     * 除法
     * @param value1
     * @param value2
     * @param <T>
     * @return
     * @throws IllegalAccessException
     */
    public static <T extends Number> BigDecimal div(T value1,T value2) throws IllegalAccessException {
        return div(value1,value2,2);
    }

    /**
     * 除法指定四舍五入
     * @param value1
     * @param value2
     * @param scale
     * @param <T>
     * @return
     * @throws IllegalAccessException
     */
    public static <T extends Number> BigDecimal div(T value1,T value2,int scale) throws IllegalAccessException {
        if ( scale < 0 ){
            throw new IllegalAccessException ( "小数点位数需要大于0 " );
        }
        BigDecimal bigDecimal = new BigDecimal ( value1.doubleValue ( ) );
        BigDecimal bigDecimal1 = new BigDecimal ( value2.doubleValue ( ) );
        return bigDecimal.divide ( bigDecimal1 ).setScale ( scale,RoundingMode.HALF_UP );
    }

    /**
     * 保留两位小数,四舍五入
     *
     * @param number
     * @return String
     */
    public static String formatHalfUp(Number number) {
        return rounding(number, 2);
    }

    /**
     * 四舍五入
     * @param number
     * @param scale
     * @return String
     */
    public static String rounding(Number number,int scale){
        return new BigDecimal ( number.doubleValue () ).setScale ( scale, RoundingMode.HALF_UP).toString ();
    }
}
