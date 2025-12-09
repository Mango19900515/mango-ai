package com.zpmc.ai.report.service;

import com.zpmc.ai.report.agent.ReportAgent;
import com.zpmc.ai.report.model.ChartResult;
import com.zpmc.ai.report.model.ReportResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 报表服务类
 * 负责处理自然语言查询、生成SQL、执行查询和生成总结
 *
 * @author songqiang
 * @date 2025-12-02 10:06
 */
@Slf4j
@Service
public class ReportService {

    @Resource
    private ReportAgent reportAgent;
    @Resource
    private SqlExecService executor;
    @Resource
    private ChartService chartService;


    /**
     * 分析报表
     *
     * @param naturalQuery 自然语言查询
     * @return 报表结果
     */
    public ReportResult analyze(String naturalQuery) {
        // Step 1: AI 生成 SQL
        String reportSql = reportAgent.generateSql(naturalQuery);
        log.info("生成的sql:{}", reportSql);
//        reportSql = extractSql(reportSql);
        // Step 2: 执行 SQL
        List<Map<String, Object>> rows = executor.generateAndRun(reportSql);
        // Step 3: 生成柱状图数据
        ChartResult chartResult = chartService.generateChart(naturalQuery, rows);
        // Step 4: AI 生成总结
        String summary = reportAgent.generateSummary(reportSql, rows);
        // Step 5: 封装结果
        ReportResult result = new ReportResult();
        result.setSql(reportSql);
        result.setSummary(summary);
        result.setRows(rows);
        result.setChart(chartResult);
        return result;
    }

    static String extractSql(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "";
        }
        // 匹配 ```sql ... ``` 代码块
        Pattern codeBlockPattern = Pattern.compile("(?is)```\\s*sql\\s*(.*?)```");
        Matcher matcher = codeBlockPattern.matcher(raw);
        if (matcher.find()) {
            String sql = matcher.group(1);

            // 去掉行首注释 "-- " 和多余空白
            String[] lines = sql.split("\\r?\\n");
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                line = line.replaceAll("^\\s*--.*", "").trim(); // 去掉注释行
                if (!line.isEmpty()) {
                    sb.append(line).append(" ");
                }
            }
            return sb.toString().trim();
        }

        // 如果没匹配到代码块，也尝试匹配普通 SQL
        Pattern sqlPattern = Pattern.compile("(?im)^(select|insert|update|delete)\\b.*?;?$", Pattern.DOTALL);
        matcher = sqlPattern.matcher(raw);
        if (matcher.find()) {
            return matcher.group(0).trim();
        }

        // 没匹配到 SQL，返回空字符串
        return "";
    }





    public static void main(String args[]) {
        String sql = "SELECT \n" +
                "    QC_NAME AS \"岸桥\",\n" +
                "    COUNT(DISTINCT VES_CODE) AS \"作业航次数\",\n" +
                "    SUM(L_D_S_MOVE) AS \"装卸船作业量_钩\",\n" +
                "    SUM(L_D_S_UNIT) AS \"装卸船作业量_Unit\",\n" +
                "    SUM(L_D_S_TEU) AS \"装卸船作业量_TEU\",\n" +
                "    SUM(HATCHCOVER_HOOK) AS \"舱盖板作业量\",\n" +
                "    SUM(SHOB_HOOK) AS \"翻箱作业量\",\n" +
                "    SUM(OTHER_HOOK) AS \"其他作业量\",\n" +
                "    ROUND(SUM(CYCLE_DURATION)/3600, 2) AS \"总作业小时_h\",\n" +
                "    ROUND(SUM(L_D_S_MOVE) / NULLIF(ROUND(SUM(CYCLE_DURATION)/3600,2), 0), 2) AS \"总效率_钩_h\",\n" +
                "    ROUND(SUM(L_D_S_UNIT) / NULLIF(ROUND(SUM(CYCLE_DURATION)/3600,2), 0), 2) AS \"总效率_Unit_h\",\n" +
                "    ROUND(SUM(L_D_S_TEU) / NULLIF(ROUND(SUM(CYCLE_DURATION)/3600,2), 0), 2) AS \"总效率_TEU_h\",\n" +
                "    MAX(L_D_CYCLE_DURATION) AS \"最大循环时长\",\n" +
                "    ROUND(SUM(CYCLE_DURATION) / NULLIF(SUM(L_D_S_MOVE), 0), 2) AS \"平均循环时长\",\n" +
                "    MIN(L_D_CYCLE_DURATION) AS \"最小循环时长\"\n" +
                "FROM V_DWM_QC_MT_CYCLE\n" +
                "WHERE C_YEAR = 2024\n" +
                "GROUP BY QC_NAME\n" +
                "ORDER BY QC_NAME;";
        log.info(extractSql(sql));
    }

}
