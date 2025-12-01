package com.zpmc.ai.web;

import com.zpmc.ai.service.Assistant;
import com.zpmc.ai.service.StreamingAssistant;
import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

/**
 * @author songqiang
 * @date 2025-11-25 13:46
 */
@RestController
@Slf4j
public class AiRagController {

    private final StreamingAssistant streamingAssistant;

    private final Assistant assistant;

    public AiRagController(Assistant assistant, StreamingAssistant streamingAssistant) {
        this.assistant = assistant;
        this.streamingAssistant = streamingAssistant;
    }


    @GetMapping("/test")
    public String test() {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey("sk-a5a14aa75445458fbc4223ed6f5d577e")
                .modelName("qwen-plus")
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .logRequests(true)
                .logResponses(true)
                .build();
        AiServices.create(Assistant.class, model);
        return model.chat("Say Hello World");
    }

    @GetMapping("/testStream")
    public void testStream() {
        OpenAiStreamingChatModel model = OpenAiStreamingChatModel.builder()
                .apiKey("sk-a5a14aa75445458fbc4223ed6f5d577e")
                .modelName("qwen-plus")
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .logRequests(true)
                .logResponses(true)
                .build();
        String userMessage = "讲一个笑话！";
        model.chat(userMessage, new StreamingChatResponseHandler() {
            @Override
            public void onCompleteResponse(ChatResponse chatResponse) {
                System.out.println("onCompleteResponse: " + chatResponse);
            }

            @Override
            public void onPartialResponse(String partialResponse) {
                System.out.println("onPartialResponse: " + partialResponse);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println(throwable.getMessage());
            }
        });
    }


    @GetMapping(value = "/testStreamingAssistant", produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamingAssistant(
            @RequestParam(value = "message", defaultValue = "What is the current time?") String message) {
        if (message == null) {
            return Flux.error(new IllegalArgumentException("message must not be null"));
        }
        return streamingAssistant.chat(message);
    }

    @GetMapping(value = "/testRag")
    public String testRag(@RequestParam(value = "message", defaultValue = "Hello") String message) {
//        Document document = FileSystemDocumentLoader.loadDocument("D://miles-of-smiles-terms-of-use.txt");
        Document document = FileSystemDocumentLoader.loadDocument("D://zpmc.txt");
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        EmbeddingStoreIngestor.ingest(document, embeddingStore);
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.from(embeddingStore);

        ChatModel model = OpenAiChatModel.builder()
                .apiKey("sk-a5a14aa75445458fbc4223ed6f5d577e")
                .modelName("qwen-plus")
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .logRequests(true)
                .logResponses(true)
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model) // it should use OpenAI LLM
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10)) // it should remember 10 latest messages
                .contentRetriever(contentRetriever) // it should have access to our documents
                .build();

//        return assistant.chat("I had an accident, should I pay extra?");
        return assistant.chat(message);
    }



    @GetMapping(value = "/testRedisRag")
    public String testRedisRag(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        //文档 → 分段 → 向量化 → 存 Redis → 查询时向量搜索 → LLM 结合上下文回答
//        运行流程：
//            1、用户问问题
//            2、embeddingModel → 将问题向量化
//            3、到 Redis 搜最相关的内容（top3）
//            4、RAG → 将搜索内容 + 问题发给 qwen-plus
//            5、大模型给出更准确答案
//            6、assistant.chat 返回结果
        // ====== 1.向量库存储（Redis-Stack + RediSearch） =======
        RedisEmbeddingStore redisEmbeddingStore  = RedisEmbeddingStore.builder()
                .host("127.0.0.1")
                .port(6379)
                .password("zpmc123")
                .dimension(1024)
                .indexName("zpmc_index")
                .build();


        // ====== 2.阿里云向量模型 创建阿里云 embedding 模型（用于算向量） ======
        //作用：将文本转成向量
        EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey("sk-c857ce5f9f87497aa4b49af868f02432")
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .modelName("text-embedding-v4")
                .build();

        // ====== 加载文档（如果已经录入过，可以跳过 ingestor） ======
        Document document = FileSystemDocumentLoader.loadDocument("D://zpmc.txt");

        //档切片 + 向量化 + 存到 Redis（ingestor）
//        作用：自动完成以下工作：
//        (1) 把文档按 500 字切片，重叠 50 字（避免上下文断裂）
//        (2) 每个切片计算向量（embeddingModel 完成）
//        (3) 把向量 + 文本存入 Redis 的向量索引里
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(redisEmbeddingStore)
                .documentSplitter(DocumentSplitters.recursive(500, 50))
                .build();
        ingestor.ingest(document);



        // ====== RAG 检索器 创建 RAG 检索器（向量搜索）======
//        作用：当用户提问时：例如你问：IGV 不能定位时怎么处理？
//        步骤：
//        (1)把问题 embedding → 向量
//        (2)在 Redis 中搜最相似的 3 段内容（maxResults=3）
//        (3)把这 3 段内容返回给大模型（qwen-plus）作为上下文
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(redisEmbeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .build();

        // ====== Qwen Plus Chat 模型 调用阿里云大模型进行自然语言回答======
        ChatModel chatModel = OpenAiChatModel.builder()
                .apiKey("sk-a5a14aa75445458fbc4223ed6f5d577e")
                .modelName("qwen-plus")
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .logRequests(true)
                .logResponses(true)
                .build();


        // ====== AI 服务 ======
//        把所有组件串起来：
//            ChatModel（回答问题）
//            RAG Retriever（提供知识库上下文）
//            Memory（记住过去 10 次对话）
//            Assistant（生成最终服务）
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(contentRetriever)
                .build();
        return assistant.chat(message);
    }
}
