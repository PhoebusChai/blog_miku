package com.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReadingHistory {
    private Long id;
    private Integer userId;
    private Integer articleId;
    private Integer isLiked; // 0-否，1-是
    private Integer isBookmarked; // 0-否，1-是
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 关联字段（非数据库字段）
    private String articleTitle;
    private String articleSummary;
    private String articleCover;
}
