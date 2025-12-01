package com.zpmc.ai.service;

import dev.langchain4j.rag.content.Content;
import dev.langchain4j.service.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * AiCodeHelperServiceTest
 *
 * @author songqiang
 * @date 2025/11/28 10:23
 */
@Slf4j
@SpringBootTest
class AiCodeHelperServiceTest {

    @Resource
    private AiCodeHelperService aiCodeHelperService;

    @Test
    void chat() {
        String chat = aiCodeHelperService.chat("你好，我是程序员鱼皮");
        log.info(chat);
//        String chat1 = aiCodeHelperService.chat(1, "你好，我是程序员鱼皮");
//        log.info(chat1);
//        String chat2 = aiCodeHelperService.chat(2, "你好，我是程序员宋强");
//        log.info(chat2);
    }

    @Test
    void chatWithMemory() {
        String result = aiCodeHelperService.chat("你好，我是程序员鱼皮");
        log.info(result);
        result = aiCodeHelperService.chat("你好，我是谁来着？");
        log.info(result);
    }
    @Test
    void chatForReport(){
        String userMessage = "你好，我是程序员鱼皮，学编程两年半，请帮我制定学习报告";
        AiCodeHelperService.Report report = aiCodeHelperService.chatForReport(userMessage);
        System.out.println(report);
    }

    @Test
    void chatWithRag(){
        Result<String> result = aiCodeHelperService.chatWithRag("怎么学习 Java？有哪些常见面试题？");
        log.info(result.toString());
    }
    @Test
    void chatWithTools(){
        String chat = aiCodeHelperService.chat(2, "有哪些常见的计算机网络面试题");
        log.info(chat);
    }
    @Test
    void chatWithMcp(){
        String result = aiCodeHelperService.chat("什么是程序员鱼皮的编程导航");
        log.info(result);
    }

    @Test
    void chatWithGuardrail(){
        String result = aiCodeHelperService.chat("kill the game");
        log.info(result);
    }

}