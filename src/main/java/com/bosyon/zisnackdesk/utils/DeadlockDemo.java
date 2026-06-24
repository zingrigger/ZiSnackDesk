package com.bosyon.zisnackdesk.utils;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 设计一个经典的账户转账死锁场景：
 * 两个账户 A 和 B，线程 1 从 A 转给 B，线程 2 从 B 转给 A，两者加锁顺序相反。
 * 分别在平台线程和虚拟线程下运行，观察死锁行为，并使用 jcmd 分析线程状态
 *
 * jcmd 找到对应 pid
 * 然后导出对应日志
 * jcmd pid Thread.dump_to_file -l deadlockdemo.txt
 * 留意：parking to wait for
 */
public class DeadlockDemo {
    static class Account {
        private final String name;
        private final ReentrantLock lock = new ReentrantLock();
        private int balance = 1000;

        Account(String name) { this.name = name; }

        void transfer(Account to, int amount) {
            System.out.println(Thread.currentThread() + " 尝试获取锁: " + this.name);
            this.lock.lock();
            try {
                System.out.println(Thread.currentThread() + " 成功锁定: " + this.name + "，准备获取: " + to.name);

                // 【核心改进】故意睡 50ms，强制让另一个线程把另一把锁拿走，制造死锁
                try { Thread.sleep(50); } catch (InterruptedException ignored) {}

                to.lock.lock();
                try {
                    this.balance -= amount;
                    to.balance += amount;
                } finally {
                    to.lock.unlock();
                }
            } finally {
                this.lock.unlock();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Account a = new Account("Account-A"), b = new Account("Account-B");

        // ======= 模式切换：可以通过修改布尔值分别测试 =======
        boolean useVirtualThread = false;

        if (useVirtualThread) {
            System.out.println("=== 正在运行：虚拟线程死锁测试 ===");
            Thread t1 = Thread.startVirtualThread(() -> a.transfer(b, 100));
            Thread t2 = Thread.startVirtualThread(() -> b.transfer(a, 100));

            // 虚拟线程是守护线程，主线程不睡死的话，程序会直接退出
            Thread.sleep(Long.MAX_VALUE);
        } else {
            System.out.println("=== 正在运行：平台线程死锁测试 ===");
            Thread t1 = new Thread(() -> a.transfer(b, 100), "Platform-Thread-1");
            Thread t2 = new Thread(() -> b.transfer(a, 100), "Platform-Thread-2");
            t1.start();
            t2.start();
        }
    }
}