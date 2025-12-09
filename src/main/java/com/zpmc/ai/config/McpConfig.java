//package com.zpmc.ai.config;
//
//import dev.langchain4j.mcp.McpToolProvider;
//import dev.langchain4j.mcp.client.DefaultMcpClient;
//import dev.langchain4j.mcp.client.McpClient;
//import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * MCP配置
// *
// * @author songqiang
// * @date 2025-12-01 9:54
// */
//@Configuration
//public class McpConfig {
//
//    @Value("${bigmodel.api-key}")
//    private String apiKey;
//
//    @Bean
//    public McpToolProvider mcpToolProvider(){
//        // 和 MCP 服务通讯
//        HttpMcpTransport httpMcpTransport = new HttpMcpTransport.Builder()
//                .sseUrl("https://open.bigmodel.cn/api/mcp/web_search/sse?Authorization=" + apiKey)
//                .logRequests(true)
//                .logResponses(true)
//                .build();
//        // 创建 MCP 客户端
//        McpClient mcpClient = new DefaultMcpClient.Builder()
//                .key("sqMcpClient")
//                .transport(httpMcpTransport)
//                .build();
//        // 从 MCP 客户端获取工具
//        McpToolProvider toolProvider = McpToolProvider.builder()
//                .mcpClients(mcpClient)
//                .build();
//        return toolProvider;
//
//    }
//}
