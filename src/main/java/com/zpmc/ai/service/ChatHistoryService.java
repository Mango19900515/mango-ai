package com.zpmc.ai.service;

import com.alibaba.fastjson.JSON;
import com.zpmc.ai.dal.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ChatHistoryService
 *
 * @author songqiang
 * @date 2025-11-20 14:53
 */
@Service
public class ChatHistoryService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String CHAT_KEY_PREFIX = "chat:session:";

    public void saveMessage(String sessionId, ChatMessage message) {
        String key = CHAT_KEY_PREFIX + sessionId;
        redisTemplate.opsForList().rightPush(key, toJson(message));
    }

    public List<ChatMessage> getHistory(String sessionId) {
        String key = CHAT_KEY_PREFIX + sessionId;
        List<String> list = redisTemplate.opsForList().range(key, 0, -1);

        return list.stream()
                .map(this::fromJson)
                .collect(Collectors.toList());
    }

    private String toJson(ChatMessage msg) {
        return JSON.toJSONString(msg);
    }

    private ChatMessage fromJson(String json) {
        return JSON.parseObject(json, ChatMessage.class);
    }

}
