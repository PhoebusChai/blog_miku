package com.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Project {
    private Integer id;
    private String title;
    private String description;
    private String coverImage;
    private String demoUrl;
    private String githubUrl;
    private String techStack;
    private Integer status;
    private Integer sortOrder;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
