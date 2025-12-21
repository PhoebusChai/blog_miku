package com.blog.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.blog.common.Result;
import com.blog.entity.Comment;
import com.blog.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comments")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    /**
     * 获取文章评论列表
     */
    @GetMapping("/article/{articleId}")
    public Result<Map<String, Object>> getArticleComments(@PathVariable Integer articleId) {
        List<Map<String, Object>> comments = commentService.getArticleComments(articleId);
        Map<String, Object> result = new HashMap<>();
        result.put("comments", comments);
        result.put("total", comments.size());
        return Result.success(result);
    }
    
    /**
     * 获取留言板评论列表
     */
    @GetMapping("/guestbook")
    public Result<Map<String, Object>> getGuestbookComments() {
        List<Map<String, Object>> comments = commentService.getGuestbookComments();
        Map<String, Object> result = new HashMap<>();
        result.put("comments", comments);
        result.put("total", comments.size());
        return Result.success(result);
    }
    
    /**
     * 创建评论
     */
    @PostMapping
    public Result<Comment> createComment(@RequestBody Comment comment, HttpServletRequest request) {
        // 获取IP地址
        String ipAddress = getClientIpAddress(request);
        comment.setIpAddress(ipAddress);
        
        // 如果用户已登录，使用用户ID
        if (StpUtil.isLogin()) {
            comment.setUserId(StpUtil.getLoginIdAsInt());
            // 清空游客信息
            comment.setGuestName(null);
            comment.setGuestEmail(null);
        }
        
        return Result.success(commentService.createComment(comment));
    }
    
    /**
     * 点赞评论
     */
    @PostMapping("/{id}/like")
    public Result<Void> likeComment(@PathVariable Integer id) {
        commentService.likeComment(id);
        return Result.success();
    }
    
    /**
     * 取消点赞
     */
    @PostMapping("/{id}/unlike")
    public Result<Void> unlikeComment(@PathVariable Integer id) {
        commentService.unlikeComment(id);
        return Result.success();
    }
    
    /**
     * 删除评论（需要登录，只能删除自己的评论或管理员可以删除任何评论）
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Integer id) {
        StpUtil.checkLogin();
        
        Comment comment = commentService.getById(id);
        if (comment == null) {
            return Result.error("评论不存在");
        }
        
        // 检查权限：只能删除自己的评论，或者管理员可以删除任何评论
        Integer currentUserId = StpUtil.getLoginIdAsInt();
        boolean isAdmin = StpUtil.hasRole("admin");
        
        if (!isAdmin && !currentUserId.equals(comment.getUserId())) {
            return Result.error("无权删除此评论");
        }
        
        commentService.removeById(id);
        return Result.success();
    }
    
    /**
     * 获取所有评论（管理员）
     * 支持分页、状态筛选、文章筛选、搜索
     */
    @GetMapping("/admin/list")
    public Result<Map<String, Object>> getAdminComments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer articleId,
            @RequestParam(required = false) String keyword) {
        StpUtil.checkRole("admin");
        return Result.success(commentService.getAdminComments(page, pageSize, status, articleId, keyword));
    }
    
    /**
     * 获取评论统计（管理员）
     */
    @GetMapping("/admin/stats")
    public Result<Map<String, Object>> getCommentStats() {
        StpUtil.checkRole("admin");
        return Result.success(commentService.getCommentStats());
    }
    
    /**
     * 审核评论 - 通过
     */
    @PutMapping("/{id}/approve")
    public Result<Void> approveComment(@PathVariable Integer id) {
        StpUtil.checkRole("admin");
        commentService.updateStatus(id, 1);
        return Result.success();
    }
    
    /**
     * 审核评论 - 拒绝
     */
    @PutMapping("/{id}/reject")
    public Result<Void> rejectComment(@PathVariable Integer id) {
        StpUtil.checkRole("admin");
        commentService.updateStatus(id, 2);
        return Result.success();
    }
    
    /**
     * 标记为垃圾评论
     */
    @PutMapping("/{id}/spam")
    public Result<Void> markAsSpam(@PathVariable Integer id) {
        StpUtil.checkRole("admin");
        commentService.updateStatus(id, 3);
        return Result.success();
    }
    
    /**
     * 批量删除评论
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Integer> ids) {
        StpUtil.checkRole("admin");
        commentService.batchDelete(ids);
        return Result.success();
    }
    
    /**
     * 批量审核评论
     */
    @PutMapping("/batch/approve")
    public Result<Void> batchApprove(@RequestBody List<Integer> ids) {
        StpUtil.checkRole("admin");
        commentService.batchUpdateStatus(ids, 1);
        return Result.success();
    }
    
    /**
     * 批量标记为垃圾评论
     */
    @PutMapping("/batch/spam")
    public Result<Void> batchMarkAsSpam(@RequestBody List<Integer> ids) {
        StpUtil.checkRole("admin");
        commentService.batchUpdateStatus(ids, 3);
        return Result.success();
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
