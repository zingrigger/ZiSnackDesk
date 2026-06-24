package com.bosyon.zisnackdesk.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateDangerDemo {

    // 所有人共享这一个不安全的 format 对象
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        // 准备 5 个不同的日期字符串
        String[] dates = {
                "2026-01-01 12:00:00",
                "2027-02-02 13:15:00",
                "2028-03-03 14:30:00",
                "2029-04-04 15:45:00",
                "2030-05-05 16:50:00"
        };

        for (int i = 0; i < 5; i++) {
            final String dateStr = dates[i];
            new Thread(() -> {
                try {
                    // 多个线程并发去解析【不同】的字符串
                    Date parsedDate = sdf.parse(dateStr);
                    System.out.println(Thread.currentThread().getName()
                            + " 应该解析: " + dateStr
                            + " -> 实际解析出: " + sdf.format(parsedDate));
                } catch (Exception e) {
                    // 运行后你大概率会在这里看到：java.lang.NumberFormatException
                    System.err.println(Thread.currentThread().getName() + " 挂了！报错原因: " + e.getMessage());
                }
            }, "Thread-" + i).start();
        }
    }

}
