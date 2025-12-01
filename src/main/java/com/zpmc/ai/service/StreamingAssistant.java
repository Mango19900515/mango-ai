package com.zpmc.ai.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;
import reactor.core.publisher.Flux;

/**
 * LangChain4j的流式接口
 *
 * @author songqiang
 * @date 2025-11-20 10:39
 */
@AiService
public interface StreamingAssistant {


    @SystemMessage("You are a polite assistant")
    Flux<String> chat(String userMessage);
}
