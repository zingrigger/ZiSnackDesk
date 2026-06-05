package com.bosyon.zisnackdesk;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.bosyon.zisnackdesk.mapper")
public class ZiSnackDeskApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZiSnackDeskApplication.class, args);
    }

}
