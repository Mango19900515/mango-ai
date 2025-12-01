package com.zpmc.ai.web;

import com.zpmc.ai.dal.ChatMessage;
import com.zpmc.ai.service.AiCodeHelperService;
import com.zpmc.ai.service.Assistant;
import com.zpmc.ai.service.ChatService;
import com.zpmc.ai.service.StreamingAssistant;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

/**
 * Ai接口
 *
 * @author songqiang
 * @date 2025-11-20 9:24
 */
@RestController
public class AiController {
    private final Assistant assistant;
    private final StreamingAssistant streamingAssistant;

    @Resource
    private AiCodeHelperService aiCodeHelperService;

    @Autowired
    private ChatService chatService;

    public AiController(Assistant assistant, StreamingAssistant streamingAssistant) {
        this.assistant = assistant;
        this.streamingAssistant = streamingAssistant;
    }

    /**
     * 聊天接口
     *
     * @param message 用户消息
     * @return 模型回复
     */
    @GetMapping("/assistant")
    public String assistant(@RequestParam(value = "message", defaultValue = "What is the current time?") String message) {
        return assistant.chat(message);
    }

    /**
     * 流式聊天接口
     *
     * @param message 用户消息
     * @return 模型回复流
     */
    @GetMapping(value = "/streamingAssistant", produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamingAssistant(
            @RequestParam(value = "message", defaultValue = "What is the current time?") String message) {
        return streamingAssistant.chat(message);
    }

    /**
     * 聊天接口
     *
     * @param sessionId 会话ID
     * @param message   用户消息
     * @return 模型回复
     */
    @GetMapping("/chat")
    public String chat(@RequestParam("sessionId") String sessionId, @RequestParam("message") String message) {
        return chatService.chat(sessionId, message);
    }

    /**
     * 获取聊天历史记录
     *
     * @param sessionId 会话ID
     * @return 聊天记录列表
     */
    @GetMapping("/getChatHistory")
    public List<ChatMessage> getChatHistory(@RequestParam("sessionId") String sessionId) {
        return chatService.getChatHistory(sessionId);
    }


    @GetMapping("/chatStream")
    public Flux<ServerSentEvent<String>> chatStream(@RequestParam("memoryId") int memoryId, @RequestParam("message") String message) {
        return aiCodeHelperService.chatStream(memoryId, message)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }

}
