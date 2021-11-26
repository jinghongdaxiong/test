package com.jiyu.AtomicReference;

import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

/**
 * <b>功能名：AtomicReferenceDemo1</b><br>
 * <b>说明：</b><br>
 * <b>著作权：</b> Copyright (C) 2021 HUIFANEDU  CORPORATION<br>
 * <b>修改履历：
 *
 * @author 2021-11-26 xuxiongzi
 */
@Data
public class AtomicReferenceDemo1 {

    // 定义为 volatile 修饰的变量
    volatile static DebitCard debitCard = new DebitCard("zhangSan", 10);

    public static void main(String[] args) {
        IntStream.range(0, 10).forEach(i -> new Thread(() -> {
            while (true) {
                // 读取全局的 debitCard 对象
                final DebitCard debitCard1 = debitCard;
                // 基于全局的 debitCard 加10构建一个新的对象
                DebitCard newDc = new DebitCard(debitCard1.getName(), debitCard1.getAccount() + 10);
                // 把新建的都行赋值给 全局的变量
                debitCard = newDc;
                System.out.println(newDc);
                try {
                    TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(20));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "T-" + i).start());

    }

    @Test
    void test1() {
        IntStream.range(0, 10).forEach(i -> new Thread(() -> {
            while (true) {
                // 加 锁 操作
                synchronized (AtomicReferenceDemo1.class) {
                    // 读取全局的 debitCard 对象
                    final DebitCard debitCard1 = debitCard;
                    // 基于全局的 debitCard 加10构建一个新的对象
                    DebitCard newDc = new DebitCard(debitCard1.getName(), debitCard1.getAccount() + 10);

                    // 把新建的都行赋值给 全局的变量
                    debitCard = newDc;
                    System.out.println(newDc);
                    try {
                        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(10));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "T-" + i).start());

    }
    static AtomicReference<DebitCard> ref = new AtomicReference(new DebitCard("zhangSan", 10));

    @Test
    void test3() {

        IntStream.range(0, 10).forEach(i -> new Thread(() -> {

            while (true){
                DebitCard debitCard = ref.get();
                DebitCard newDc = new DebitCard(debitCard.getName(), debitCard.getAccount() + 10);
                if(ref.compareAndSet(debitCard, newDc)){
                    System.out.println(Thread.currentThread().getName() + " 当前值为： " + newDc.toString() + " " + System.currentTimeMillis());
                }
                long sleepValue = (long) (Math.random() * 10000);
                try {
                    Thread.sleep(sleepValue);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "T-" + i).start());
    }
}