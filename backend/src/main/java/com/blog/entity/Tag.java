package com.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Tag {
    private Integer id;
    private String name;
    private String slug;
    private Integer articleCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
