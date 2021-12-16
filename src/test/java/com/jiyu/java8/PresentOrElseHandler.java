package com.jiyu.java8;

import java.util.function.Consumer;

/**
 * <b>功能名：PresentOrElseHandler</b><br>
 * <b>说明：</b><br>
 * <b>著作权：</b> Copyright (C) 2021 HUIFANEDU  CORPORATION<br>
 * <b>修改履历：
 *
 * @author 2021-12-16 xuxiongzi
 */

public interface PresentOrElseHandler<T extends Object> {
    /**
     * 值不为空时执行消费操作
     * 值为空时执行其他的操作
     *
     * @param action 值不为空时，执行的消费操作
     * @param emptyAction 值为空时，执行的操作
     **/
    void presentOrElseHandle(Consumer<? super T> action, Runnable emptyAction);

}