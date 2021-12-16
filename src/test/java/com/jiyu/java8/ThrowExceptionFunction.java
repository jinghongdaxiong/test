package com.jiyu.java8;

/**
 * <b>功能名：ThrowExceptionFunction</b><br>
 * <b>说明：</b><br>
 * <b>著作权：</b> Copyright (C) 2021 HUIFANEDU  CORPORATION<br>
 * <b>修改履历：
 *
 * @author 2021-12-16 xuxiongzi
 */
@FunctionalInterface
public interface ThrowExceptionFunction {
    /**
     * 抛出异常信息
     *
     * @param message 异常信息
     **/
    void throwMessage(String message);
}