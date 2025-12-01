package com.zpmc.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 启动入口类
 */
@SpringBootApplication
@Slf4j
public class ZpmcAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZpmcAiApplication.class, args);
        log.info("ZpmcAiApplication 启动成功!!!");
        // ====== Redis 作为向量库 =======

    }
}
