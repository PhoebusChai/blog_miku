package com.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SiteConfig {
    private Integer id;
    private String configKey;
    private String configValue;
    private Integer configType; // 0-字符串，1-数字，2-布尔值，3-JSON
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
