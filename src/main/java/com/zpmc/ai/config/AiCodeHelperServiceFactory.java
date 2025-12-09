package com.zpmc.ai.config;

import com.zpmc.ai.report.agent.ReportAgent;
import com.zpmc.ai.service.AiCodeHelperService;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 模型工厂
 * 负责创建和配置各种AI服务实例
 *
 * @author songqiang
 * @date 2025-11-28 10:11
 */
@Configuration
public class AiCodeHelperServiceFactory {
    // 常量定义
    private static final int MAX_MEMORY_MESSAGES = 10;

    @Resource
    private ChatModel qwenChatModel;

    @Resource
    private StreamingChatModel qwenStreamingChatModel;

    @Resource
    private ContentRetriever contentRetriever;

//    @Resource
//    private McpToolProvider mcpToolProvider;

    /**
     * 创建AI代码助手服务实例
     * 配置了聊天模型、流式模型、会话记忆和内容检索器
     *
     * @return 配置好的AiCodeHelperService实例
     */
    @Bean
    public AiCodeHelperService aiCodeHelperService() {
        //2.0 回话记忆
        AiCodeHelperService aiCodeHelperService = AiServices.builder(AiCodeHelperService.class)
                .chatModel(qwenChatModel)
                //流式接口
                .streamingChatModel(qwenStreamingChatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(MAX_MEMORY_MESSAGES))
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(MAX_MEMORY_MESSAGES))
                //智能检索增强
                .contentRetriever(contentRetriever)
                //工具调用
//                .tools(new InterviewQuestionTool())
                //mcp工具调用
//                .toolProvider(mcpToolProvider)
                .build();
        return aiCodeHelperService;
    }


    /**
     * 创建报表代理实例
     * 整合了SQL生成和结果总结功能
     *
     * @return 配置好的ReportAgent实例
     */
    @Bean
    public ReportAgent reportAgent() {


//        return AiServices.builder(ReportAgent.class)
//                .chatModel(qwenChatModel)
//                .build();
        return AiServices.builder(ReportAgent.class)
                .chatModel(qwenChatModel)
                //流式接口
                .streamingChatModel(qwenStreamingChatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(MAX_MEMORY_MESSAGES))
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(MAX_MEMORY_MESSAGES))
                //智能检索增强
                .contentRetriever(contentRetriever)
                //工具调用
//                .tools(new InterviewQuestionTool())
                //mcp工具调用
//                .toolProvider(mcpToolProvider)
                .build();
    }

}
