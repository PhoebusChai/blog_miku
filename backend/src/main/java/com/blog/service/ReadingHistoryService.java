package com.blog.service;

import com.blog.entity.ReadingHistory;
import com.blog.mapper.ReadingHistoryMapper;
import com.blog.mapper.ArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReadingHistoryService {
    
    @Autowired
    private ReadingHistoryMapper readingHistoryMapper;
    
    @Autowired
    private ArticleMapper articleMapper;
    
    /**
     * 记录阅读历史
     */
    public void recordReading(Integer userId, Integer articleId) {
        if (userId != null && articleId != null) {
            readingHistoryMapper.insertOrUpdate(userId, articleId);
        }
    }
    
    /**
     * 获取用户对文章的阅读记录
     */
    public ReadingHistory getByUserAndArticle(Integer userId, Integer articleId) {
        if (userId == null || articleId == null) {
            return null;
        }
        return readingHistoryMapper.selectByUserAndArticle(userId, articleId);
    }
    
    /**
     * 获取用户阅读记录列表
     */
    public List<ReadingHistory> getReadingHistory(Integer userId, Integer limit) {
        return readingHistoryMapper.selectByUserId(userId, limit);
    }
    
    /**
     * 获取用户点赞的文章
     */
    public List<ReadingHistory> getLikedArticles(Integer userId) {
        return readingHistoryMapper.selectLikedByUserId(userId);
    }
    
    /**
     * 获取用户收藏的文章
     */
    public List<ReadingHistory> getBookmarkedArticles(Integer userId) {
        return readingHistoryMapper.selectBookmarkedByUserId(userId);
    }
    
    /**
     * 点赞文章
     */
    @Transactional
    public void likeArticle(Integer userId, Integer articleId) {
        // 查询是否已有阅读记录
        ReadingHistory history = readingHistoryMapper.selectByUserAndArticle(userId, articleId);
        
        if (history == null) {
            // 创建新记录
            history = new ReadingHistory();
            history.setUserId(userId);
            history.setArticleId(articleId);
            history.setIsLiked(1);
            history.setIsBookmarked(0);
            readingHistoryMapper.insert(history);
        } else if (history.getIsLiked() == 0) {
            // 更新为已点赞
            readingHistoryMapper.updateLikeStatus(userId, articleId, 1);
        } else {
            // 已经点赞，不做处理
            return;
        }
        
        // 增加文章点赞数
        articleMapper.incrementLikeCount(articleId);
    }
    
    /**
     * 取消点赞文章
     */
    @Transactional
    public void unlikeArticle(Integer userId, Integer articleId) {
        ReadingHistory history = readingHistoryMapper.selectByUserAndArticle(userId, articleId);
        
        if (history != null && history.getIsLiked() == 1) {
            // 更新为未点赞
            readingHistoryMapper.updateLikeStatus(userId, articleId, 0);
            
            // 减少文章点赞数
            articleMapper.decrementLikeCount(articleId);
        }
    }
    
    /**
     * 收藏文章
     */
    @Transactional
    public void bookmarkArticle(Integer userId, Integer articleId) {
        // 查询是否已有阅读记录
        ReadingHistory history = readingHistoryMapper.selectByUserAndArticle(userId, articleId);
        
        if (history == null) {
            // 创建新记录
            history = new ReadingHistory();
            history.setUserId(userId);
            history.setArticleId(articleId);
            history.setIsLiked(0);
            history.setIsBookmarked(1);
            readingHistoryMapper.insert(history);
        } else if (history.getIsBookmarked() == 0) {
            // 更新为已收藏
            readingHistoryMapper.updateBookmarkStatus(userId, articleId, 1);
        }
    }
    
    /**
     * 取消收藏文章
     */
    @Transactional
    public void unbookmarkArticle(Integer userId, Integer articleId) {
        ReadingHistory history = readingHistoryMapper.selectByUserAndArticle(userId, articleId);
        
        if (history != null && history.getIsBookmarked() == 1) {
            // 更新为未收藏
            readingHistoryMapper.updateBookmarkStatus(userId, articleId, 0);
        }
    }
}
