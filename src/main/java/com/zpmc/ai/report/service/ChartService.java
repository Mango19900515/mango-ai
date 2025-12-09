package com.zpmc.ai.report.service;

import com.zpmc.ai.report.model.ChartResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 柱状图生成
 *
 * @author songqiang
 * @date 2025-12-02 9:15
 */
@Slf4j
@Service
public class ChartService {
    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 生成表格数据
     *
     * @author songqiang
     * @date 2025/12/2 16:32
     */
    public ChartResult generateChart(String naturalQuery, List<Map<String, Object>> rows) {
        if (CollectionUtils.isEmpty(rows)) {
            return new ChartResult();
        }
        ChartResult chart = new ChartResult();
        chart.setType("line");

        chart.setTitle(naturalQuery);
        List<String> labels = new ArrayList<>();
        List<Number> values = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            for (Map.Entry<String, Object> map : row.entrySet()) {
                labels.add(map.getKey());
                Object valueObj = map.getValue();
                if (valueObj instanceof Number) {
                    values.add((Number) valueObj);
                } else if (valueObj != null) {
                    try {
                        values.add(new BigDecimal(valueObj.toString()));
                    } catch (Exception e) {
                        values.add(0);  // 解析失败当 0
                    }
                } else {
                    values.add(0);
                }
            }
        }
        chart.setLabels(labels);
        chart.setValues(values);
        chart.generateSimpleSeries();
        return chart;
    }

}
