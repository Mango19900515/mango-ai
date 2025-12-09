package com.zpmc.ai.report.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Sql执行 服务
 *
 * @author songqiang
 * @date 2025-12-02 9:15
 */
@Slf4j
@Service
public class SqlExecService {
    @Resource
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> generateAndRun(String sql) {
        // 2. 安全检查（强烈推荐）
        validate(sql);
        // 3. 执行 SQL
        if (sql.trim().toLowerCase().startsWith("select") || sql.trim().toLowerCase().startsWith("with")) {
            return jdbcTemplate.queryForList(sql);
        }
        log.error("不支持的sql!");
        return null;
    }

    // 简单风控
    private void validate(String sql) {
        String lower = sql.toLowerCase();
        if (lower.contains("drop") || lower.contains("truncate")) {
            throw new RuntimeException("危险 SQL 禁止执行: " + sql);
        }
    }
}
