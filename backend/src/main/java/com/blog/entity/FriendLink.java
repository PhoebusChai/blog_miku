package com.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FriendLink {
    private Integer id;
    private String name;
    private String url;
    private String avatar;
    private String description;
    private Integer status;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
