package com.jiyu.java8;

import org.junit.jupiter.api.Test;

/**
 * <b>功能名：TestVUtils</b><br>
 * <b>说明：</b><br>
 * <b>著作权：</b> Copyright (C) 2021 HUIFANEDU  CORPORATION<br>
 * <b>修改履历：
 *
 * @author 2021-12-16 xuxiongzi
 */

public class TestVUtils {
    @Test
    void isTrue1() {
        VUtils.isTure(false).throwMessage("俺要抛出异常了！");
    }

    @Test
    void isTrue2() {
        VUtils.isTure(true).throwMessage("俺要抛出异常了！");
    }

    @Test
    void isTrueOrFalse() {
        VUtils.isTureOrFalse(false).trueOrFalseHandle(
                () -> System.out.println("true, 俺要开始秀了"),
                () -> System.out.println("false, 秀不动了，快跑")
        );
    }

    @Test
    void isBlankOrNoBlack() {
        VUtils.isBlankOrNoBlank("hello").
                presentOrElseHandle(System.out::println,
                        () -> System.out.println("空字符串"));
    }
}