package com.jiyu.AtomicReference;

/**
 * <b>功能名：DebitCard</b><br>
 * <b>说明：</b><br>
 * <b>著作权：</b> Copyright (C) 2021 HUIFANEDU  CORPORATION<br>
 * <b>修改履历：
 *
 * @author 2021-11-26 xuxiongzi
 */
public class DebitCard {
    private final String name;
    private final int account;
    public DebitCard(String name, int account) {
        this.name = name;
        this.account = account;
    }
    public String getName() {
        return name;
    }

    public int getAccount() {
        return account;
    }

    @Override
    public String toString() {
        return "DebitCard {name=\""+name+"\"," +
                "account="+account+"}";
    }
}