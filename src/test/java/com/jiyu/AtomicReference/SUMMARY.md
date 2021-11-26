# 并发编程 — 原子类 AtomicReference 详解

通过对 AtomicInteger、AtomicBoolean 和 AtomicLong 分析我们发现，这三个原子类只能对单个变量进行原子操作，那么我们如果要对多个变量进行原子操作，这三个类就无法实现了。那如果要进行多个变量进行原子操作呢？操作方式就是，先把 多个变量封装成一个类，然后通过 AtomicReference 进行操作。

众所周知，对象的引用其实是一个4字节的数字，代表着在JVM堆内存中的引用地址，对一个4字节数字的读取操作和写入操作本身就是原子性的，通常情况下，我们对对象引用的操作一般都是获取该引用或者重新赋值（写入操作），我们也没有办法对对象引用的4字节数字进行加减乘除运算，那么为什么JDK要提供AtomicReference类用于支持引用类型的原子性操作呢？

## 1、AtomicReference的应用场景
这里通过设计一个个人银行账号资金变化的场景，逐渐引入AtomicReference的使用，该实例有些特殊，需要满足如下几点要求。

个人账号被设计为不可变对象，一旦创建就无法进行修改。
个人账号类只包含两个字段：账号名、现金数字。
为了便于验证，我们约定个人账号的现金只能增多而不能减少。
根据前两个要求，我们简单设计一个代表个人银行账号的Java类DebitCard，该类将被设计为不可变。

````
 
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
````

### 1.1、通过 volatile 修饰的多线程 
````
 
public class AtomicReferenceDemo1 {
 
    // 定义为 volatile 修饰的变量
    volatile static  DebitCard debitCard = new DebitCard("zhangSan", 10);
 
    public static void main(String[] args) {
        IntStream.range(0, 10).forEach(i -> {
            new Thread(() -> {
                while (true){
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
            }, "T-" + i).start();
        });
    }
}
````
### 1.2、通过加锁的多线程
针对上面的问题，我们首先想到的可能是通过加锁(synchronized 或 Lock )解决，如下代码所示：

````
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
````

### 1.3、 AtomicReference的非阻塞解决方案
第2小节中的方案似乎满足了我们的需求，但是它却是一种阻塞式的解决方案，同一时刻只能有一个线程真正在工作，其他线程都将陷入阻塞，因此这并不是一种效率很高的解决方案，这个时候就可以利用 AtomicReference 的非阻塞原子性解决方案提供更加高效的方式了。

````
public class AtomicReferenceDemo1 {
 
    static AtomicReference<DebitCard> ref = new AtomicReference(new DebitCard("zhangSan", 10));
 
    public static void main(String[] args) {
 
 
 
        IntStream.range(0, 10).forEach(i -> {
            new Thread(() -> {
 
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
            }, "T-" + i).start();
        });
    }
}
````

## 2、AtomicReference的基本用法

### 2.1、创建 AtomicReference
 AtomicReference是一个泛型类，它的构造与其他原子类型的构造一样，也提供了无参和一个有参的构造函数。

* AtomicReference()：当使用无参构造函数创建AtomicReference对象的时候，需要再次调用set()方法为AtomicReference内部的value指定初始值。
* AtomicReference(V initialValue)：创建AtomicReference对象时顺便指定初始值。

### 2.2、常用方法
* compareAndSet(V expect, V update)：原子性地更新AtomicReference内部的value值，其中expect代表当前AtomicReference的value值，update则是需要设置的新引用值。该方法会返回一个boolean的结果，当expect和AtomicReference的当前值不相等时，修改会失败，返回值为false，若修改成功则会返回true。
* getAndSet(V newValue)：原子性地更新AtomicReference内部的value值，并且返回AtomicReference的旧值。
* getAndUpdate(UnaryOperator<V> updateFunction)：原子性地更新value值，并且返回AtomicReference的旧值，该方法需要传入一个Function接口。
* updateAndGet(UnaryOperator<V> updateFunction)：原子性地更新value值，并且返回AtomicReference更新后的新值，该方法需要传入一个Function接口。
* getAndAccumulate(V x, BinaryOperator<V> accumulatorFunction)：原子性地更新value值，并且返回AtomicReference更新前的旧值。该方法需要传入两个参数，第一个是更新后的新值，第二个是BinaryOperator接口。
* getAndAccumulate(V x, BinaryOperator<V> accumulatorFunction)：原子性地更新value值，并且返回AtomicReference更新前的旧值。该方法需要传入两个参数，第一个是更新后的新值，第二个是BinaryOperator接口。
* accumulateAndGet(V x, BinaryOperator<V> accumulatorFunction)：原子性地更新value值，并且返回AtomicReference更新后的值。该方法需要传入两个参数，第一个是更新的新值，第二个是BinaryOperator接口。
* get()：获取AtomicReference的当前对象引用值。
* set(V newValue)：设置AtomicReference最新的对象引用值，该新值的更新对其他线程立即可见。
* lazySet(V newValue)：设置AtomicReference的对象引用值。lazySet方法的原理已经在AtomicInteger中介绍过了，这里不再赘述。

## 3、AtomicReference的内幕
在AtomicReference类中，最关键的方法为compareAndSet()，下面来一探该方法的内幕。

AtomicReference中的方法
````
public final boolean compareAndSet(V expect, V update) {
    return unsafe.compareAndSwapObject(this, valueOffset, expect, update);
}
````
Unsafe中的代码
````
public final native boolean compareAndSwapObject(Object var1, long var2, Object var4, Object var5);
````

## 4、总结
AtomicReference 类的使用比较简单，但是需要了解 AtomicReference 类的使用场景，才能在开发中更好的使用 AtomicReference。