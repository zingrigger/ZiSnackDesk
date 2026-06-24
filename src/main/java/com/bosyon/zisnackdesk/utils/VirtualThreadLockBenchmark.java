package com.bosyon.zisnackdesk.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class VirtualThreadLockBenchmark {

    private static final int THREAD_COUNT = 1000;
    private static final int IO_MOCK_TIME_MS = 10;

    private static final Object syncLock = new Object();
    private static final ReentrantLock reentrantLock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 开始虚拟线程锁竞争基准测试 ===");
        System.out.println("当前运行 JDK 版本: " + System.getProperty("java.version"));
        System.out.println("可用 CPU 核心数 (默认载体线程数): " + Runtime.getRuntime().availableProcessors());
        System.out.println("------------------------------------------------");

        // 1. 测试 synchronized 版本
        runTest("Synchronized 锁", () -> {
            synchronized (syncLock) {
                mockIO();
            }
        });

        System.out.println("------------------------------------------------");

        // 2. 测试 ReentrantLock 版本
        runTest("ReentrantLock 锁", () -> {
            reentrantLock.lock();
            try {
                mockIO();
            } finally {
                reentrantLock.unlock();
            }
        });
    }

    private static void runTest(String testName, Runnable lockStrategy) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);// 计数器
        AtomicInteger completedTasks = new AtomicInteger(0);// 成功计数

        long startTime = System.currentTimeMillis();// 掐表计时开始

        // 启动 1000 个虚拟线程
        for (int i = 0; i < THREAD_COUNT; i++) {
            Thread.startVirtualThread(() -> {
                try {
                    lockStrategy.run(); // 执行传进来的加锁和 I/O 动作
                    completedTasks.incrementAndGet(); // 任务完成数 + 1
                } finally {
                    latch.countDown();// 倒计时减 1（不管成功失败都会执行）
                }
            });
        }

        // 在主线程中定时采样载体线程（Carrier Thread）的状态
        // 载体线程通常名字叫 "ForkJoinPool-1-worker-X"
        Thread monitorThread = Thread.startVirtualThread(() -> {
            // 拿到 JVM 的线程管理器
            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            try {
                // 只要 THREAD_COUNT 个虚拟线程还没全部干完，监工就一直巡逻
                while (latch.getCount() > 0) {
                    int activeCarrierThreads = 0;
                    long[] threadIds = threadMXBean.getAllThreadIds();
                    ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadIds);

                    // 遍历当前 JVM 里的所有线程
                    for (ThreadInfo info : threadInfos) {
                        // 必须是虚拟线程的载体线程。状态必须是“正在吃CPU狂飙”
                        if (info != null && info.getThreadName().contains("ForkJoinPool")
                                && info.getThreadState() == Thread.State.RUNNABLE) {
                            activeCarrierThreads++;
                        }
                    }
                    // 打印当前活跃（处于 RUNNABLE 状态）的载体线程数
                    // \r 的意思是“回到行首覆盖打印”，这样控制台数字就会动态变化
                    System.out.printf("[%s 监控] 活跃载体线程数 (Worker): %d\r", testName, activeCarrierThreads);
                    // 没必要一直死循环盯着，每隔 100 毫秒看一次
                    Thread.sleep(100);
                }
            } catch (InterruptedException ignored) {
            }
        });

        // 等待所有虚拟线程执行完毕，主线程在这里死等，直到 1000 个虚拟线程全部 countDown 完
        latch.await();
        monitorThread.interrupt(); // 停止监控

        long endTime = System.currentTimeMillis();
        long durationMs = endTime - startTime;

        // 计算吞吐量：每秒完成任务数 (TPS)， 吞吐量公式： 总任务数 / 总秒数
        double tps = (completedTasks.get() / (durationMs / 1000.0));

        System.out.println(); // 换行
        System.out.printf("结果 -> %s 测试完成! 耗时: %d ms, 吞吐量 (TPS): %.2f%n",
                testName, durationMs, tps);
    }

    private static void mockIO() {
        try {
            Thread.sleep(IO_MOCK_TIME_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
