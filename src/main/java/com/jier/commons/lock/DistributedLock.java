package com.jier.commons.lock;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁注解类
 * 
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {

    /**
     * 锁的名称。
     * 如果lockName可以确定，直接设置该属性。
     */
    String lockName() default "";

    /**
     * lockName前缀
     */
    String lockNamePre() default "";

    /**
     * lockName后缀
     */
    String lockNamePost() default "";

    /**
     * 获得锁名时拼接前后缀用到的分隔符
     * 
     * @return
     */
    String separator() default "-";

    /**
     * 获取注解的方法参数列表的某个参数对象的某个属性值来作为lockName。因为有时候lockName是不固定的。
     * 当param不为空时，可以通过argNum参数来设置具体是参数列表的第几个参数，不设置则默认取第一个。
     */
    String param() default "";

    /**
     * 将方法第argNum个参数作为锁
     */
    int argNum() default 1;

    /**
     * 是否使用公平锁。
     * 公平锁即先来先得。
     */
    boolean fairLock() default false;

    /**
     * 是否使用尝试锁。
     */
    boolean tryLock() default false;

    /**
     * 最长等待时间。
     * 该字段只有当tryLock()返回true才有效。
     */
    long waitTime() default 60L;

    /**
     * 锁超时时间。
     * 超时时间过后，锁自动释放。
     * 建议：
     * 尽量缩简需要加锁的逻辑。
     */
    long leaseTime() default 60L;

    /**
     * 时间单位。默认为秒。
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
