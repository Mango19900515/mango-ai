package com.zpmc.ai.report.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.util.List;
import java.util.Map;

/**
 * 报表接口
 *
 * @author songqiang
 * @date 2025-12-03 15:52
 */
public interface ReportAgent {
    /**
     * 根据自然语言生成SQL查询
     */
    @SystemMessage(fromResource = "kpi-prompt.txt")
    @UserMessage("生成 SQL：{{query}}")
    String generateSql(String query);

    /**
     * 根据SQL查询结果生成自然语言总结
     */
    @SystemMessage("""
                你是一个专业 BI 数据分析师。
                根据 SQL 查询结果（rows），用自然语言总结数据洞察。
                输出简短、有价值的总结话术。
            """)
    @UserMessage("""
                SQL: {{sql}}
                数据行: {{rows}}
                请生成自然语言总结：
            """)
    String generateSummary(
            @V("sql") String sql,
            @V("rows") List<Map<String, Object>> rows
    );
}
