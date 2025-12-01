package com.zpmc.ai.service;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Classname TestServiceTest
 * @Description TODO
 * @Date 2025/11/27 8:40
 * @Created by songqiang
 */
@SpringBootTest
class TestServiceTest {

    @Resource
    private TestService testService;

    @Test
    void sayHello() {
        String sq = testService.sayHello("sq");
        System.out.println(sq);
    }
}