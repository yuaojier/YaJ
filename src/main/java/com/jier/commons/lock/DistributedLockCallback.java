package com.jier.commons.lock;


public interface DistributedLockCallback<T> {

    /**
     * 调用者必须在此方法中实现需要加分布式锁的业务逻辑
     *
     * @return
     */
    T process();

    /**
     * 默认加锁失败时执行方法。
     *
     * @return
     */
    default T fail() throws Exception {
        throw new Exception ( "aaa" );
    }

    /**
     * 得到分布式锁名称
     *
     * @return
     */
    String getLockName();

}
