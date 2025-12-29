package com.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Gallery {
    private Integer id;
    private String title;
    private String description;
    private String imageUrl;
    private String thumbnailUrl;
    private String category;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
