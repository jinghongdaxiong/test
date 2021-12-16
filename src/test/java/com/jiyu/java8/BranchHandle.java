package com.jiyu.java8;

/**
 * <b>功能名：BranchHandle</b><br>
 * <b>说明：</b><br>
 * <b>著作权：</b> Copyright (C) 2021 HUIFANEDU  CORPORATION<br>
 * <b>修改履历：
 *
 * @author 2021-12-16 xuxiongzi
 */

@FunctionalInterface
public interface BranchHandle {
    /**
     * 分支操作
     *
     * @param trueHandle 为true时要进行的操作
     * @param falseHandle 为false时要进行的操作
     * @return void
     **/
    void trueOrFalseHandle(Runnable trueHandle, Runnable falseHandle);

}