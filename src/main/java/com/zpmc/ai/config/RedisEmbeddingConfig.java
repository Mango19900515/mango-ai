//package com.zpmc.ai.config;
//
//import dev.langchain4j.store.embedding.redis.RedisEmbeddingStore;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @author songqiang
// * @date 2025-11-26 8:50
// */
//@Configuration
//public class RedisEmbeddingConfig {
//    @Bean
//    public RedisEmbeddingStore redisEmbeddingStore() {
//        return RedisEmbeddingStore.builder()
//                .host("127.0.0.1")
//                .port(6379)
//                .password("123456")
//                .indexName("zpmc_index")
//                .dimension(1024)
//                .build();
//    }
//}
