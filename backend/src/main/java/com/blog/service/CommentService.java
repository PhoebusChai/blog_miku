package com.blog.service;

import com.blog.entity.Comment;
import com.blog.mapper.CommentMapper;
import com.blog.mapper.ArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentService {
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private ArticleMapper articleMapper;
    
    /**
     * 获取文章评论列表
     */
    public List<Map<String, Object>> getArticleComments(Integer articleId) {
        return commentMapper.getArticleComments(articleId);
    }
    
    /**
     * 获取留言板评论列表
     */
    public List<Map<String, Object>> getGuestbookComments() {
        return commentMapper.getGuestbookComments();
    }
    
    /**
     * 创建评论
     */
    @Transactional
    public Comment createComment(Comment comment) {
        // 默认状态为已通过
        if (comment.getStatus() == null) {
            comment.setStatus(1);
        }
        // 默认点赞数为0
        if (comment.getLikeCount() == null) {
            comment.setLikeCount(0);
        }
        commentMapper.insert(comment);
        
        // 如果是文章评论，更新文章的评论数
        if (comment.getArticleId() != null) {
            articleMapper.incrementCommentCount(comment.getArticleId());
        }
        
        return comment;
    }
    
    /**
     * 根据ID获取评论
     */
    public Comment getById(Integer id) {
        return commentMapper.selectById(id);
    }
    
    /**
     * 更新评论
     */
    public void updateById(Comment comment) {
        commentMapper.updateById(comment);
    }
    
    /**
     * 删除评论（包括子评论）
     */
    @Transactional
    public void removeById(Integer id) {
        Comment comment = getById(id);
        if (comment != null) {
            // 先删除所有子评论
            deleteChildComments(id);
            
            // 删除当前评论
            commentMapper.deleteById(id);
            
            // 如果是文章评论，更新文章的评论数
            if (comment.getArticleId() != null) {
                articleMapper.decrementCommentCount(comment.getArticleId());
            }
        }
    }
    
    /**
     * 递归删除子评论
     */
    private void deleteChildComments(Integer parentId) {
        List<Comment> children = commentMapper.selectByParentId(parentId);
        if (children != null && !children.isEmpty()) {
            for (Comment child : children) {
                // 递归删除子评论的子评论
                deleteChildComments(child.getId());
                // 删除子评论
                commentMapper.deleteById(child.getId());
                // 更新文章评论数
                if (child.getArticleId() != null) {
                    articleMapper.decrementCommentCount(child.getArticleId());
                }
            }
        }
    }
    
    /**
     * 点赞评论
     */
    public void likeComment(Integer commentId) {
        Comment comment = getById(commentId);
        if (comment != null) {
            comment.setLikeCount(comment.getLikeCount() + 1);
            updateById(comment);
        }
    }
    
    /**
     * 取消点赞
     */
    public void unlikeComment(Integer commentId) {
        Comment comment = getById(commentId);
        if (comment != null && comment.getLikeCount() > 0) {
            comment.setLikeCount(comment.getLikeCount() - 1);
            updateById(comment);
        }
    }
    
    /**
     * 获取所有评论（管理员）
     * 支持分页、状态筛选、文章筛选、搜索
     */
    public Map<String, Object> getAdminComments(Integer page, Integer pageSize, Integer status, Integer articleId, String keyword) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> comments = commentMapper.getAdminComments(offset, pageSize, status, articleId, keyword);
        Long total = commentMapper.countAdminComments(status, articleId, keyword);
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", comments);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        
        return result;
    }
    
    /**
     * 获取评论统计
     */
    public Map<String, Object> getCommentStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", commentMapper.countByStatus(null));
        stats.put("approved", commentMapper.countByStatus(1));
        stats.put("pending", commentMapper.countByStatus(0));
        stats.put("rejected", commentMapper.countByStatus(2));
        stats.put("spam", commentMapper.countByStatus(3));
        return stats;
    }
    
    /**
     * 更新评论状态
     */
    @Transactional
    public void updateStatus(Integer id, Integer status) {
        Comment comment = getById(id);
        if (comment != null) {
            comment.setStatus(status);
            updateById(comment);
        }
    }
    
    /**
     * 批量删除评论
     */
    @Transactional
    public void batchDelete(List<Integer> ids) {
        for (Integer id : ids) {
            removeById(id);
        }
    }
    
    /**
     * 批量更新评论状态
     */
    @Transactional
    public void batchUpdateStatus(List<Integer> ids, Integer status) {
        for (Integer id : ids) {
            updateStatus(id, status);
        }
    }
}
