package com.zpmc.ai.config;

import com.zpmc.ai.service.AiCodeHelperService;
import com.zpmc.ai.service.tools.InterviewQuestionTool;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author songqiang
 * @date 2025-11-28 10:11
 */
@Configuration
public class AiCodeHelperServiceFactory {

    @Resource
    private ChatModel qwenChatModel;

    @Resource
    private StreamingChatModel qwenStreamingChatModel;

    @Resource
    private ContentRetriever contentRetriever;
    @Resource
    private McpToolProvider mcpToolProvider;

    @Bean
    public AiCodeHelperService aiCodeHelperService() {
//        1.0 简单创建对象
//        return AiServices.create(AiCodeHelperService.class, qwenChatModel);
        //2.0 回话记忆
        MessageWindowChatMemory messageWindowChatMemory = MessageWindowChatMemory.withMaxMessages(10);
        AiCodeHelperService aiCodeHelperService = AiServices.builder(AiCodeHelperService.class)
                .chatModel(qwenChatModel)
                .streamingChatModel(qwenStreamingChatModel) //流式接口
                .chatMemory(messageWindowChatMemory)
                .chatMemoryProvider(memoryId-> MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(contentRetriever) //智能检索增强
                .tools(new InterviewQuestionTool()) //工具调用
                .toolProvider(mcpToolProvider) //mcp工具调用
                .build();
        return aiCodeHelperService;
    }
}
