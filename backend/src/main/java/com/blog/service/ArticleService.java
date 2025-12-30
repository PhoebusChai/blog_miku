package com.blog.service;

import cn.dev33.satoken.stp.StpUtil;
import com.blog.common.PageResult;
import com.blog.dto.ArticleRankingDTO;
import com.blog.entity.Article;
import com.blog.entity.Tag;
import com.blog.entity.User;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.ArticleTagMapper;
import com.blog.mapper.TagMapper;
import com.blog.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ArticleService {
    
    private final ArticleMapper articleMapper;
    private final UserMapper userMapper;
    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;
    
    public PageResult<Article> getPublishedList(Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<Article> articles = articleMapper.selectPublishedList(offset, pageSize);
        
        // 填充作者信息和标签
        for (Article article : articles) {
            User author = userMapper.selectById(article.getAuthorId());
            article.setAuthor(author);
            
            List<Tag> tags = tagMapper.selectByArticleId(article.getId());
            article.setTags(tags);
        }
        
        Long total = articleMapper.countPublished();
        return new PageResult<>(total, articles);
    }
    
    public Article getById(Integer id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        
        // 增加浏览量
        articleMapper.incrementViewCount(id);
        
        // 填充作者信息和标签
        User author = userMapper.selectById(article.getAuthorId());
        article.setAuthor(author);
        
        List<Tag> tags = tagMapper.selectByArticleId(article.getId());
        article.setTags(tags);
        
        return article;
    }
    
    @Transactional
    public Article create(Article article) {
        int userId = StpUtil.getLoginIdAsInt();
        article.setAuthorId(userId);
        
        if (article.getStatus() == 1) {
            article.setPublishedAt(LocalDateTime.now());
        }
        
        articleMapper.insert(article);
        
        // 保存标签关联
        if (article.getTags() != null && !article.getTags().isEmpty()) {
            for (Tag tag : article.getTags()) {
                articleTagMapper.insert(article.getId(), tag.getId());
                tagMapper.incrementArticleCount(tag.getId());
            }
        }
        
        return article;
    }
    
    @Transactional
    public Article update(Integer id, Article article) {
        Article existing = articleMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("文章不存在");
        }
        
        int userId = StpUtil.getLoginIdAsInt();
        if (!existing.getAuthorId().equals(userId) && !StpUtil.hasRole("admin")) {
            throw new RuntimeException("无权限修改此文章");
        }
        
        article.setId(id);
        
        // 如果从草稿变为发布，设置发布时间
        if (existing.getStatus() != 1 && article.getStatus() == 1) {
            article.setPublishedAt(LocalDateTime.now());
        }
        
        articleMapper.update(article);
        
        // 更新标签关联
        if (article.getTags() != null) {
            // 删除旧的标签关联
            List<Tag> oldTags = tagMapper.selectByArticleId(id);
            for (Tag oldTag : oldTags) {
                tagMapper.decrementArticleCount(oldTag.getId());
            }
            articleTagMapper.deleteByArticleId(id);
            
            // 添加新的标签关联
            for (Tag tag : article.getTags()) {
                articleTagMapper.insert(id, tag.getId());
                tagMapper.incrementArticleCount(tag.getId());
            }
        }
        
        return articleMapper.selectById(id);
    }
    
    @Transactional
    public void delete(Integer id) {
        Article existing = articleMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("文章不存在");
        }
        
        int userId = StpUtil.getLoginIdAsInt();
        if (!existing.getAuthorId().equals(userId) && !StpUtil.hasRole("admin")) {
            throw new RuntimeException("无权限删除此文章");
        }
        
        // 删除标签关联并更新标签计数
        List<Tag> tags = tagMapper.selectByArticleId(id);
        for (Tag tag : tags) {
            tagMapper.decrementArticleCount(tag.getId());
        }
        articleTagMapper.deleteByArticleId(id);
        
        articleMapper.deleteById(id);
    }
    
    public PageResult<Article> getMyArticles(Integer page, Integer pageSize) {
        int userId = StpUtil.getLoginIdAsInt();
        int offset = (page - 1) * pageSize;
        
        List<Article> articles = articleMapper.selectByAuthor(userId, offset, pageSize);
        
        // 填充标签
        for (Article article : articles) {
            List<Tag> tags = tagMapper.selectByArticleId(article.getId());
            article.setTags(tags);
        }
        
        // 这里简化处理，实际应该有专门的 count 方法
        Long total = (long) articles.size();
        return new PageResult<>(total, articles);
    }
    
    public Map<String, Integer> getPublishHeatmap() {
        List<Map<String, Object>> data = articleMapper.selectPublishHeatmap();
        Map<String, Integer> result = new HashMap<>();
        
        for (Map<String, Object> item : data) {
            String date = item.get("date").toString();
            Integer count = ((Number) item.get("count")).intValue();
            result.put(date, count);
        }
        
        return result;
    }
    
    /**
     * 获取网站公开统计数据
     */
    public Map<String, Object> getPublicStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("articles", articleMapper.countPublished());
        stats.put("views", articleMapper.sumViewCount());
        stats.put("likes", articleMapper.sumLikeCount());
        stats.put("comments", articleMapper.sumCommentCount());
        return stats;
    }
    
    public List<ArticleRankingDTO> getTopArticles(Integer limit) {
        List<Map<String, Object>> data = articleMapper.selectTopByViews(limit);
        List<ArticleRankingDTO> result = new ArrayList<>();
        
        for (Map<String, Object> item : data) {
            ArticleRankingDTO dto = new ArticleRankingDTO();
            dto.setId(((Number) item.get("id")).intValue());
            dto.setTitle(item.get("title").toString());
            dto.setViewCount(((Number) item.get("view_count")).intValue());
            result.add(dto);
        }
        
        return result;
    }
    
    /**
     * 获取精选文章
     * 优先返回置顶文章，不足时用浏览数最多的文章补充
     */
    public List<Article> getFeaturedArticles(Integer limit) {
        List<Article> articles = articleMapper.selectFeaturedArticles(limit);
        
        // 填充作者信息和标签
        for (Article article : articles) {
            User author = userMapper.selectById(article.getAuthorId());
            article.setAuthor(author);
            
            List<Tag> tags = tagMapper.selectByArticleId(article.getId());
            article.setTags(tags);
        }
        
        return articles;
    }
    
    /**
     * 获取归档文章列表
     * 支持搜索、标签筛选、排序
     */
    public List<Article> getArchiveArticles(String keyword, List<Integer> tagIds, String sortBy) {
        List<Article> articles = articleMapper.selectArchiveArticles(keyword, tagIds, sortBy);
        
        // 填充作者信息和标签
        for (Article article : articles) {
            User author = userMapper.selectById(article.getAuthorId());
            article.setAuthor(author);
            
            List<Tag> tags = tagMapper.selectByArticleId(article.getId());
            article.setTags(tags);
        }
        
        return articles;
    }
}
