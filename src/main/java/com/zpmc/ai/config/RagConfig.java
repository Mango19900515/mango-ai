package com.zpmc.ai.config;

import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.List;

/**
 * 检索增强生成配置
 *
 * @author songqiang
 * @date 2025-11-28 15:42
 */
@Slf4j
@Configuration
public class RagConfig {

    private static final String DOCUMENTS_LOADED_FLAG = "rag:documents:loaded";

    private boolean documentsLoaded = false;


    private final RagProperties props;

    public RagConfig(RagProperties props) {
        this.props = props;
    }

    @Resource
    private EmbeddingStore<TextSegment> embeddingStore;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @PostConstruct
    public void initializeDocuments() {
        try {
//             检查Redis中是否已有文档加载标志
            if (redisTemplate != null && Boolean.TRUE.equals(redisTemplate.hasKey(DOCUMENTS_LOADED_FLAG))) {
                log.info("文档已经加载到Redis中，跳过加载过程");
                documentsLoaded = true;
                return;
            }
            log.info("开始加载文档到Redis...");
            // 1. 加载文档
            List<Document> documents = FileSystemDocumentLoader.loadDocuments("src/main/resources/docs");
            if (documents.isEmpty()) {
                log.warn("未找到文档，跳过加载过程");
                return;
            }
            // 2. 文档切割：将每个文档按每段进行分割，最大 1000 字符，每次重叠最多 200 个字符
            DocumentByParagraphSplitter paragraphSplitter = new DocumentByParagraphSplitter(1000, 200);
            // 创建嵌入模型
            EmbeddingModel qwenEmbeddingModel = OpenAiEmbeddingModel.builder()
                    .apiKey(props.getEmbedding().getApiKey())
                    .baseUrl(props.getEmbedding().getBaseUrl())
                    .modelName(props.getEmbedding().getModel())
                    .build();

            // 如果没有注入embeddingStore，则创建新的Redis连接
            EmbeddingStore<TextSegment> storeToUse = RedisEmbeddingStore.builder()
                    .host(props.getRedis().getHost())
                    .port(props.getRedis().getPort())
                    .password(props.getRedis().getPassword())
                    .dimension(props.getEmbedding().getDimension())
                    .indexName(props.getRedis().getIndexName())
                    .build();

//            searchRedis(storeToUse, qwenEmbeddingModel);

            // 3. 创建文档加载器
            EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                    .documentSplitter(paragraphSplitter)
                    // 为了提高搜索质量，为每个 TextSegment 添加文档名称
                    .textSegmentTransformer(textSegment -> TextSegment.from(
                            textSegment.metadata().getString("file_name") + "\n" + textSegment.text(),
                            textSegment.metadata()
                    ))
                    .embeddingModel(qwenEmbeddingModel)
                    .embeddingStore(storeToUse)
                    .build();

            // 加载文档,开始向量化 + 写入 Redis
            ingestor.ingest(documents);
            log.info("成功加载 " + documents.size() + " 个文档到Redis");

            // 设置加载标志到Redis，有效期30天
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(DOCUMENTS_LOADED_FLAG, true, Duration.ofDays(30));
            }
            documentsLoaded = true;

        } catch (Exception e) {
            log.error("加载文档到Redis时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void searchRedis(EmbeddingStore<TextSegment> storeToUse, EmbeddingModel qwenEmbeddingModel) {
        TextSegment segment1 = TextSegment.from("I like football.");
        Embedding embedding1 = qwenEmbeddingModel.embed(segment1).content();
        storeToUse.add(embedding1, segment1);
        TextSegment segment2 = TextSegment.from("The weather is good today.");
        Embedding embedding2 = qwenEmbeddingModel.embed(segment2).content();
        storeToUse.add(embedding2, segment2);

        Embedding queryEmbedding = qwenEmbeddingModel.embed("What is your favourite sport?").content();
        EmbeddingSearchRequest embeddingSearchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(5)
                .minScore(0.0)
                .build();

        System.out.println(embedding1.vector().length);
        List<EmbeddingMatch<TextSegment>> matches = storeToUse.search(embeddingSearchRequest).matches();
        EmbeddingMatch<TextSegment> embeddingMatch = matches.get(0);
        System.out.println(embeddingMatch.score()); // 0.8144288659095
        System.out.println(embeddingMatch.embedded().text()); // I like football.
    }

    @Bean
    public ContentRetriever contentRetriever() {
        // 确保嵌入模型配置
        EmbeddingModel qwenEmbeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey(props.getEmbedding().getApiKey())
                .baseUrl(props.getEmbedding().getBaseUrl())
                .modelName(props.getEmbedding().getModel())
                .build();

        // 如果没有注入embeddingStore，则创建新的Redis连接
        EmbeddingStore<TextSegment> storeToUse = RedisEmbeddingStore.builder()
                .host(props.getRedis().getHost())
                .port(props.getRedis().getPort())
                .password(props.getRedis().getPassword())
                .dimension(props.getEmbedding().getDimension())
                .indexName(props.getRedis().getIndexName())
                .build();

        // 创建内容检索器
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(storeToUse)
                .embeddingModel(qwenEmbeddingModel)
                .maxResults(5) // 最多 5 个检索结果
                .minScore(0.75) // 过滤掉分数小于 0.75 的结果
                .build();

        return contentRetriever;
    }

}
