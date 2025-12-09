package com.zpmc.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * RAG配置
 *
 * @author songqiang
 * @date 2025-12-09 15:57
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "rag")
public class RagProperties {
    private Embedding embedding = new Embedding();
    private Redis redis = new Redis();

    @Data
    public static class Embedding {
        private String apiKey;
        private String baseUrl;
        private String model;
        private Integer dimension;
    }

    @Data
    public static class Redis {
        private String host;
        private Integer port;
        private String password;
        private String indexName;
    }
}
