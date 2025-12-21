package com.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Integer id;
    private Integer articleId;
    private Integer userId;
    private Integer parentId;
    private String guestName;
    private String guestEmail;
    private String content;
    private String ipAddress;
    private Integer likeCount;
    private Integer status; // 0-待审核，1-已通过，2-已拒绝，3-垃圾评论
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
