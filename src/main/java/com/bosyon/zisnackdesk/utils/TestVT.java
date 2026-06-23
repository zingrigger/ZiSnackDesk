package com.bosyon.zisnackdesk.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class TestVT {


    public static void main(String[] args) {
        // IO 密集型（默认）
        for (int i = 0; i < 100; i++) {
            Thread.startVirtualThread(() -> {
                // 模拟 I/O
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {
                }
                // 少量计算
                int x = 0;
                for (int j = 0; j < 1000; j++) x += j;
            });
        }

        // CPU 密集型（显式标记）
        for (int i = 0; i < 100; i++) {
            Thread vt = Thread.startVirtualThread(() -> {
                // 纯计算
                long sum = 0;
                for (long j = 0; j < 10_000_000; j++) sum += j;
            });
//            vt.setSchedulerHint(Thread.VirtualThreadSchedulerHint.CPU_BOUNDED);
        }


    }

    public void l3_1() {
        Thread vt = Thread.ofVirtual()
                .name("my-vt-1")
                .uncaughtExceptionHandler((t, e) -> System.err.println("Error in " + t + ": " + e))
                .unstarted(() -> {
                    // 业务逻辑
                    System.out.println("Virtual: " + Thread.currentThread());
                });
        vt.start();  // 手动启动

        Thread vt2 = Thread.ofVirtual()
                .name("my-vt-2")
                .start(() -> {
                    // 业务逻辑
                    System.out.println("Virtual: " + Thread.currentThread());
                });

        // 等待两个虚拟线程完成
        try {
            vt.join();
            vt2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void l3_2() {
        // 1. startVirtualThread
        for (int i = 0; i < 10; i++) {
            Thread.startVirtualThread(() ->
                    System.out.println("1: " + Thread.currentThread()));
        }

        // 2. ofVirtual().unstarted
        for (int i = 0; i < 10; i++) {
            Thread vt = Thread.ofVirtual()
                    .name("unstarted-" + i)
                    .unstarted(() ->
                            System.out.println("2: " + Thread.currentThread()));
            vt.start();
        }

        // 3. ofVirtual().start
        for (int i = 0; i < 10; i++) {
            Thread.ofVirtual()
                    .name("chain-" + i)
                    .start(() ->
                            System.out.println("3: " + Thread.currentThread()));
        }

        // 4. ExecutorService
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 10; i++) {
                int idx = i;
                executor.submit(() ->
                        System.out.println("4: " + Thread.currentThread() + ", idx=" + idx));
            }
        }  // 自动等待所有任务完成

        try {
            Thread.sleep(1000); // 等待所有输出
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void l2() {
        // 示例代码
        for (int i = 0; i < 20; i++) {
            Thread.startVirtualThread(() -> {
                System.out.println("Virtual: " + Thread.currentThread());
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException ignored) {
                }
            });
        }
        try {
            Thread.sleep(300000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public void l1() {
        // 1. 固定线程池（1000 个线程）服务器，监听 28080 端口
        new Thread(() -> startServer(Executors.newFixedThreadPool(1000), 28080)).start();

        // 2. 虚拟线程（每任务新虚拟线程）服务器，监听 28081 端口
        new Thread(() -> startServer(Executors.newVirtualThreadPerTaskExecutor(), 28081)).start();

        // 主线程退出后，两个服务器线程（非守护）仍保持运行

        // 使用信号量而非线程池来限制对某个资源的并发访问

//        Semaphore semaphore = new Semaphore(100000); // 限制最大并发数为100000
//
//        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
//            for (int i = 0; i < 10_000; i++) {
//                executor.submit(() -> {
//                    semaphore.acquire(); // 获取许可，若已达上限则阻塞等待
//                    try {
//                        // 访问受保护的资源或执行需要限流的操作
//                        callLimitedService();
//                    } finally {
//                        semaphore.release(); // 释放许可
//                    }
//                });
//            }
//        }
    }


    /**
     * 启动一个 Echo 服务器
     *
     * @param executor 用于处理客户端连接的线程池
     * @param port     监听端口
     */
    private static void startServer(ExecutorService executor, int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.printf("[%s] Echo server started on port %d%n",
                    executor.getClass().getSimpleName(), port);
            while (true) {
                Socket client = serverSocket.accept();
                // 将客户端处理任务提交给线程池
                executor.submit(() -> handleClient(client));
            }
        } catch (IOException e) {
            System.err.println("Server error on port " + port + ": " + e.getMessage());
        } finally {
            executor.shutdown(); // 通常不会执行到，因为循环无限
        }
    }

    /**
     * 处理单个客户端连接：逐行读取并回显
     */
    private static void handleClient(Socket client) {
        try (var in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             var out = new PrintWriter(client.getOutputStream(), true)) {
            String line;
            while ((line = in.readLine()) != null) {
                out.println("Echo: " + line);   // 原样返回，加前缀便于辨识
            }
        } catch (IOException e) {
            // 客户端断开或异常，忽略
        }
    }

}
