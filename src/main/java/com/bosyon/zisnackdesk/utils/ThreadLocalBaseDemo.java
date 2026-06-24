package com.bosyon.zisnackdesk.utils;

public class ThreadLocalBaseDemo {
    // 1. 创建一个 ThreadLocal 变量，初始值为 "空储物柜"
    private static final ThreadLocal<String> locker = ThreadLocal.withInitial(() -> "Empty");

    public static void main(String[] args) {
        // 线程 1：存入自己的秘密
        new Thread(() -> {
            locker.set("线程1的秘密");
            System.out.println(Thread.currentThread().getName() + " 查看到: " + locker.get());
            locker.remove(); // 用完清空
        }, "Thread-A").start();

        // 线程 2：直接去查看（它只能看到初始值，看不到线程 1 的秘密）
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " 查看到: " + locker.get());
        }, "Thread-B").start();
    }
}
