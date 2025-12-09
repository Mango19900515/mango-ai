package com.zpmc.ai.report.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zpmc.ai.report.model.ChartResult;
import com.zpmc.ai.report.model.ReportResult;
import com.zpmc.ai.report.service.ChartService;
import com.zpmc.ai.report.service.ReportService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 报表接口
 *
 * @author songqiang
 * @date 2025-12-02 10:08
 */
@Slf4j
@RestController
public class ReportController {

    @Resource
    private ReportService service;

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Resource
    private ChartService chartService;

    /**
     * 查询报表
     *
     * @param query 自然语言查询
     * @return 报表结果
     */
    @PostMapping("/analyze")
    public ReportResult analyze(@RequestParam String query) {
        return service.analyze(query);
    }

    /**
     * 查询报表
     *
     * @param query 自然语言查询
     * @return 报表结果
     */
    @GetMapping("/analyze")
    public ReportResult queryTable(@RequestParam String query) {
        return service.analyze(query);
    }


    /**
     * 测试查询
     *
     * @return 报表结果
     */
    @GetMapping("/testQuery")
    public ReportResult testQuery() {
        try {
//            TimeUnit.SECONDS.sleep(5);
            log.info("调用测试数据开始！！！");
            // 从classpath读取testResult.json文件
            ClassPathResource resource = new ClassPathResource("testResult.json");
            try (InputStream inputStream = resource.getInputStream()) {
                // 解析JSON文件
                JsonNode rootNode = objectMapper.readTree(inputStream);
                // 创建ReportResult对象
                ReportResult result = new ReportResult();
                // 设置SQL
                if (rootNode.has("sql")) {
                    result.setSql(rootNode.get("sql").asText());
                }
                // 设置数据行
                if (rootNode.has("rows")) {
                    List<Map<String, Object>> rows = new ArrayList<>();
                    JsonNode rowsNode = rootNode.get("rows");

                    for (JsonNode rowNode : rowsNode) {
                        Map<String, Object> row = new HashMap<>();
                        Iterator<Map.Entry<String, JsonNode>> fields = rowNode.fields();
                        while (fields.hasNext()) {
                            Map.Entry<String, JsonNode> field = fields.next();
                            String key = field.getKey();
                            JsonNode valueNode = field.getValue();

                            // 根据值的类型进行转换
                            Object value;
                            if (valueNode.isNull()) {
                                value = null;
                            } else if (valueNode.isInt()) {
                                value = valueNode.asInt();
                            } else if (valueNode.isDouble()) {
                                value = valueNode.asDouble();
                            } else if (valueNode.isLong()) {
                                value = valueNode.asLong();
                            } else if (valueNode.isBoolean()) {
                                value = valueNode.asBoolean();
                            } else {
                                value = valueNode.asText();
                            }

                            row.put(key, value);
                        }

                        rows.add(row);
                    }

                    result.setRows(rows);
                   ChartResult chartResult =  generateChartFromRows(rows);
                    // 设置chart属性
                    result.setChart(chartResult);
                }

                // 设置总结
                result.setSummary("这是2025年5月和6月的岸桥作业数据统计，包含了装卸船作业量、效率、循环时长等关键指标。");

                return result;
            }
        } catch (Exception e) {
            // 如果读取文件失败，返回默认的ReportResult对象
            ReportResult defaultResult = new ReportResult();
            defaultResult.setSummary("读取测试数据失败: " + e.getMessage());
            return defaultResult;
        }
    }

    /**
     * 根据数据行生成图表数据
     *
     * @param rows 数据行
     * @return 图表结果
     */
    private ChartResult generateChartFromRows(List<Map<String, Object>> rows) {
        return chartService.generateChart("测试标题",rows);
    }
}
