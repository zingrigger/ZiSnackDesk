package com.bosyon.zisnackdesk.controller;

import com.bosyon.zisnackdesk.mapper.SysUserMapper;
import com.bosyon.zisnackdesk.model.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;


@RequestMapping("/test")
@RestController
@Slf4j
public class TestController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private SysUserMapper sysUserMapper;


    @GetMapping("/redis")
    public String test() {
        String test = String.valueOf(System.currentTimeMillis() + "_test");
        redisTemplate.opsForValue().set("test", test, 60, TimeUnit.SECONDS);
        return test;
    }

    @GetMapping("getKey/{key}")
    public String getKey(@PathVariable String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    @GetMapping("/testSelect")
    public void testSelect() {
        log.info("----- selectAll method test ------");
        List<SysUser> userList = sysUserMapper.selectList(null);
        userList.forEach(System.out::println);
    }


}
