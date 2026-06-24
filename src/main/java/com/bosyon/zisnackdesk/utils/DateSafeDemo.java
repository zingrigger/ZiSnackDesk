package com.bosyon.zisnackdesk.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateSafeDemo {

    // 每个人各有一个储物柜，互不干扰
    private static final ThreadLocal<SimpleDateFormat> sdfLocal =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    public static void main(String[] args) {
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
                    // 从当前线程的 ThreadLocal 中获取专属的 format 实例
                    SimpleDateFormat sdf = sdfLocal.get();

                    Date parsedDate = sdf.parse(dateStr);
                    System.out.println(Thread.currentThread().getName()
                            + " 应该解析: " + dateStr
                            + " -> 实际解析出: " + sdf.format(parsedDate));
                } catch (Exception e) {
                    System.err.println("绝对不会走到这里");
                } finally {
                    // 别忘了黄金铁律：用完随手清理
                    sdfLocal.remove();
                }
            }, "Thread-" + i).start();
        }
    }

}
