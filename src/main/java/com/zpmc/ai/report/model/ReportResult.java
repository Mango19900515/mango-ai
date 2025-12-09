package com.zpmc.ai.report.model;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 报表结果
 *
 * @author songqiang
 * @date 2025-12-01 15:27
 */
@Data
public class ReportResult {

    /**
     * AI 自动生成的 SQL（便于审计、显示给用户）
     */
    private String sql;

    /**
     * 表格数据：每行就是一个 Map
     */
    private List<Map<String, Object>> rows;

    /**
     * 可视化图表数据
     */
    private ChartResult chart;

    /**
     * AI 生成的自然语言总结
     */
    private String summary;

    /**
     * 报告唯一标识
     */
    private String uuid;

    /**
     * 构造函数，生成唯一的 UUID
     */
    public ReportResult() {
        this.uuid = generateUniqueUuid();

    }

    /**
     * 生成唯一的 UUID
     *
     * @return 唯一的 UUID 字符串
     */
    private String generateUniqueUuid() {
        return UUID.randomUUID().toString();
    }

}
