package com.zpmc.ai.service;

import com.zpmc.ai.dal.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ChatService
 *
 * @author songqiang
 * @date 2025-11-20 15:03
 */
@Service
public class ChatService {

    @Autowired
    Assistant assistant;
    @Autowired
    private ChatHistoryService chatHistoryService;

    /**
     * 处理聊天请求
     *
     * @param sessionId   会话ID
     * @param userMessage 用户消息
     * @return 模型回复
     */
    public String chat(String sessionId, String userMessage) {
        // 1. 保存用户消息
        chatHistoryService.saveMessage(sessionId,
                new ChatMessage("user", userMessage, System.currentTimeMillis()));

        // 2. 调用大模型
        String reply = assistant.chat(userMessage);

        // 3. 保存模型回复
        chatHistoryService.saveMessage(sessionId,
                new ChatMessage("assistant", reply, System.currentTimeMillis()));

        return reply;
    }

    /**
     * 获取聊天历史
     *
     * @param sessionId 会话ID
     * @return 聊天历史列表
     */
    public java.util.List<ChatMessage> getChatHistory(String sessionId) {
        return chatHistoryService.getHistory(sessionId);
    }
}
