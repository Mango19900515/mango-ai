package com.zpmc.ai.service;

import dev.langchain4j.agent.tool.Tool;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/**
 * AssistantTools
 *
 * @author songqiang
 * @date 2025-11-20 10:35
 */
@Component
public class AssistantTools {
    @Tool
    @Observed
    public String currentTime() {
        return LocalTime.now().toString();
    }




}
