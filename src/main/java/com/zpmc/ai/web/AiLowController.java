package com.zpmc.ai.web;

import dev.langchain4j.model.chat.ChatModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 返回String
 *
 * @author songqiang
 * @date 2025-11-20 10:40
 */
@Controller
public class AiLowController {

    private final ChatModel chatModel;

    public AiLowController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/model")
    public String model(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        return chatModel.chat(message);
    }
}
