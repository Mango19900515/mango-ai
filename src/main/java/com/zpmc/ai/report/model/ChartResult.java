package com.zpmc.ai.report.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报表显示
 *
 * @author songqiang
 * @date 2025-12-01 15:27
 */
@Data
public class ChartResult {
    @JsonIgnore
    private String type;                 // line / bar / pie
    @JsonIgnore
    private List<String> labels;        // x轴
    @JsonIgnore// y轴
    private List<Number> values;
    private Map<String, String> title;               // 图表标题

    // 扩展字段，支持更完整的ECharts配置
    @JsonProperty("xAxis")
    private Map<String, Object> xAxis;   // x轴完整配置
    @JsonProperty("yAxis")
    private Map<String, Object> yAxis;   // y轴完整配置
    private List<Map<String, Object>> series; // 系列数据配置

    // 辅助方法：根据labels和values生成简单的series配置
    public void generateSimpleSeries() {
        if (this.labels != null && this.values != null && this.type != null) {
            Map<String, Object> seriesItem = new java.util.HashMap<>();
            seriesItem.put("data", this.values);
            seriesItem.put("type", "bar");
            this.series = List.of(seriesItem);

            // 同时生成xAxis配置
            if (this.xAxis == null) {
                this.xAxis = new java.util.HashMap<>();
                this.xAxis.put("data", this.labels);
            }
            // 同时生成yAxis配置
            if (this.yAxis == null) {
                this.yAxis = new java.util.HashMap<>();
                this.yAxis.put("type", "value");
            }
        }
    }

    public void setTitle(String text) {
        Map<String, String> titleMap = new HashMap<>(2);
        titleMap.put("text", text);
        titleMap.put("left", "center");
        this.title = titleMap;
    }
}
