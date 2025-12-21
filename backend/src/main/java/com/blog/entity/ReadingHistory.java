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
}
