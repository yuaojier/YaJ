package com.jier.commons.lock;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 注解切面
 */
@Aspect
@Component
public class DistributedLockAspect {

    @Autowired
    private DistributedLockTemplate lockTemplate;

    @Pointcut("@annotation(com.jier.commons.lock.DistributedLock)")
    public void DistributedLockAspect() {
    }

    @Around(value = "DistributedLockAspect()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        // 切点所在的类
        Class targetClass = pjp.getTarget().getClass();
        // 使用了注解的方法
        String methodName = pjp.getSignature().getName();

        Class[] parameterTypes = ((MethodSignature) pjp.getSignature()).getMethod().getParameterTypes();
        Method method = targetClass.getMethod(methodName, parameterTypes);
        Object[] arguments = pjp.getArgs();
        final String lockName = getLockName(method, arguments);

        return lock(pjp, method, lockName);
    }

    /**
     * 获取锁名称，也就是锁的key
     * 
     * @param method
     * @param args
     * @return
     */
    public String getLockName(Method method, Object[] args) {
        Objects.requireNonNull(method);
        DistributedLock annotation = method.getAnnotation(DistributedLock.class);

        String lockName = annotation.lockName(), param = annotation.param();

        // lockName存在，直接拼接前缀、后缀当key
        if (isNotEmpty(lockName)) {
            String preLockName = annotation.lockNamePre(), postLockName = annotation.lockNamePost(),
                    separator = annotation.separator();

            StringBuilder lName = new StringBuilder ();
            if (isNotEmpty(preLockName)) {
                lName.append(preLockName).append(separator);
            }
            lName.append(lockName);
            if (isNotEmpty(postLockName)) {
                lName.append(separator).append(postLockName);
            }
            lockName = lName.toString();
        } else {
            if (args.length > 0) {
                // 如果指定了参数属性，则找此参数值
                if (isNotEmpty(param)) {
                    Object arg;
                    if (annotation.argNum() > 0) {
                        arg = args[annotation.argNum() - 1];
                    } else {
                        arg = args[0];
                    }
                    lockName = String.valueOf(getParam(arg, param));

                    // 没有执定参数的属性，直接取参数的toString
                } else if (annotation.argNum() > 0) {
                    lockName = args[annotation.argNum() - 1].toString();
                }
            }
        }

        if ( StringUtils.isBlank(lockName)) {
        }
        return lockName;

    }

    /**
     * 从方法参数获取数据
     *
     * @param param
     * @param arg 方法的参数数组
     * @return
     */
    public Object getParam(Object arg, String param) {
        if (isNotEmpty(param) && arg != null) {
            try {
                Object result = PropertyUtils.getProperty(arg, param);
                return result;
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException (arg + "没有属性" + param + "或未实现get方法。", e);
            } catch (Exception e) {
                throw new RuntimeException ("", e);
            }
        }
        return null;
    }

    /**
     * 执行锁统一入口方法
     * 
     * @param pjp
     * @param method
     * @param lockName
     * @return
     */
    public Object lock(ProceedingJoinPoint pjp, Method method, final String lockName) {
        DistributedLock annotation = method.getAnnotation(DistributedLock.class);
        boolean fairLock = annotation.fairLock();
        boolean tryLock = annotation.tryLock();

        if (tryLock) {
            return tryLock(pjp, annotation, lockName, fairLock);
        } else {
            return lock(pjp, lockName, fairLock);
        }
    }

    /**
     * 直接获取锁执行方法
     * 
     * @param pjp
     * @param lockName
     * @param fairLock
     * @return
     */
    public Object lock(ProceedingJoinPoint pjp, final String lockName, boolean fairLock) {
        return lockTemplate.lock(new DistributedLockCallback<Object>() {
            @Override
            public Object process() {
                return proceed(pjp);
            }

            @Override
            public String getLockName() {
                return lockName;
            }
        }, fairLock);
    }

    /**
     * 尝试获取锁执行切入点
     * 
     * @param pjp
     * @param annotation
     * @param lockName
     * @param fairLock
     * @return
     */
    public Object tryLock(ProceedingJoinPoint pjp, DistributedLock annotation, final String lockName,
                          boolean fairLock) {

        long waitTime = annotation.waitTime(), leaseTime = annotation.leaseTime();
        TimeUnit timeUnit = annotation.timeUnit();

        return lockTemplate.tryLock(new DistributedLockCallback<Object>() {
            @Override
            public Object process() {
                return proceed(pjp);
            }

            @Override
            public String getLockName() {
                return lockName;
            }
        }, waitTime, leaseTime, timeUnit, fairLock);
    }

    /**
     * 执行切入点
     * 
     * @param pjp
     * @return
     */
    public Object proceed(ProceedingJoinPoint pjp) {
        try {
            return pjp.proceed();
            // 程序自定义的错误原样返回
        } catch (Throwable throwable) {
            throw new RuntimeException (throwable);
        }
    }

    /**
     * 判空方法，避免依赖过多第三方jar
     * 
     * @param str
     * @return
     */
    private boolean isEmpty(Object str) {
        return str == null || "".equals(str);
    }

    /**
     * 判空方法，判空方法，避免依赖过多第三方jar
     * 
     * @param str
     * @return
     */
    private boolean isNotEmpty(Object str) {
        return !isEmpty(str);
    }

}
