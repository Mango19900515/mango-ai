package com.zpmc.ai.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;


/**
 * Assistant
 *
 * @author songqiang
 * @date 2025/11/20 14:53
 */
@AiService
public interface Assistant {
    
    @SystemMessage("You are a polite assistant")
    String chat(String userMessage);
}
