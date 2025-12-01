package com.zpmc.ai.dal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ChatMessage
 *
 * @author songqiang
 * @date 2025-11-20 14:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {

    private String role;

    private String content;

    private long timestamp;
}
