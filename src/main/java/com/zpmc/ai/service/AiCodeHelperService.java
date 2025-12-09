package com.zpmc.ai.service;

import com.zpmc.ai.report.model.ReportResult;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author songqiang
 * @date 2025-11-28 10:10
 */
//输入拦截器
//@InputGuardrails({SafeInputGuardrail.class})
public interface AiCodeHelperService {

    @SystemMessage(fromResource = "system-prompt.txt")
    String chat(String userMessage);


    Flux<String> chatStream(@MemoryId int memoryId, @UserMessage String userMessage);

    //带UserId
    @SystemMessage(fromResource = "system-prompt.txt")
    String chat(@MemoryId int memoryId, @UserMessage String userMessage);


    @SystemMessage(fromResource = "system-prompt.txt")
    Report chatForReport(String userMessage);

    // 学习报告
    record Report(String name, List<String> suggestionList) {
    }

    @SystemMessage(fromResource = "system-prompt.txt")
    Result<String> chatWithRag(String userMessage);


    @SystemMessage(value = " You are a SQL generator. \n" +
            "            Generate ONLY executable SQL for MySQL.\n" +
            "            Do NOT explain, only output SQL.", fromResource = "kpi-prompt.txt")
    ReportResult queryTable(String userMessage);


}
