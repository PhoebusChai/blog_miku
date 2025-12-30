package com.blog.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.blog.common.PageResult;
import com.blog.common.Result;
import com.blog.dto.ArticleHeatmapDTO;
import com.blog.dto.ArticleRankingDTO;
import com.blog.entity.Article;
import com.blog.entity.ReadingHistory;
import com.blog.service.ArticleService;
import com.blog.service.ReadingHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {
    
    private final ArticleService articleService;
    private final ReadingHistoryService readingHistoryService;
    
    @GetMapping
    public Result<PageResult<Article>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(articleService.getPublishedList(page, pageSize));
    }
    
    @GetMapping("/my")
    public Result<PageResult<Article>> myArticles(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        StpUtil.checkLogin();
        return Result.success(articleService.getMyArticles(page, pageSize));
    }
    
    @GetMapping("/{id}")
    public Result<Article> getById(@PathVariable Integer id) {
        Article article = articleService.getById(id);
        
        // 如果用户已登录，记录阅读历史
        if (StpUtil.isLogin()) {
            Integer userId = StpUtil.getLoginIdAsInt();
            readingHistoryService.recordReading(userId, id);
        }
        
        return Result.success(article);
    }
    
    /**
     * 获取文章的用户交互状态（点赞、收藏）
     */
    @GetMapping("/{id}/status")
    public Result<Map<String, Object>> getArticleStatus(@PathVariable Integer id) {
        Map<String, Object> status = new HashMap<>();
        status.put("isLiked", false);
        status.put("isBookmarked", false);
        
        if (StpUtil.isLogin()) {
            Integer userId = StpUtil.getLoginIdAsInt();
            ReadingHistory history = readingHistoryService.getByUserAndArticle(userId, id);
            
            if (history != null) {
                status.put("isLiked", history.getIsLiked() == 1);
                status.put("isBookmarked", history.getIsBookmarked() == 1);
            }
        }
        
        return Result.success(status);
    }
    
    /**
     * 点赞文章
     */
    @PostMapping("/{id}/like")
    public Result<Void> likeArticle(@PathVariable Integer id) {
        StpUtil.checkLogin();
        Integer userId = StpUtil.getLoginIdAsInt();
        readingHistoryService.likeArticle(userId, id);
        return Result.success();
    }
    
    /**
     * 取消点赞文章
     */
    @DeleteMapping("/{id}/like")
    public Result<Void> unlikeArticle(@PathVariable Integer id) {
        StpUtil.checkLogin();
        Integer userId = StpUtil.getLoginIdAsInt();
        readingHistoryService.unlikeArticle(userId, id);
        return Result.success();
    }
    
    /**
     * 收藏文章
     */
    @PostMapping("/{id}/bookmark")
    public Result<Void> bookmarkArticle(@PathVariable Integer id) {
        StpUtil.checkLogin();
        Integer userId = StpUtil.getLoginIdAsInt();
        readingHistoryService.bookmarkArticle(userId, id);
        return Result.success();
    }
    
    /**
     * 取消收藏文章
     */
    @DeleteMapping("/{id}/bookmark")
    public Result<Void> unbookmarkArticle(@PathVariable Integer id) {
        StpUtil.checkLogin();
        Integer userId = StpUtil.getLoginIdAsInt();
        readingHistoryService.unbookmarkArticle(userId, id);
        return Result.success();
    }
    
    @PostMapping
    public Result<Article> create(@RequestBody Article article) {
        StpUtil.checkLogin();
        return Result.success(articleService.create(article));
    }
    
    @PutMapping("/{id}")
    public Result<Article> update(@PathVariable Integer id, @RequestBody Article article) {
        StpUtil.checkLogin();
        return Result.success(articleService.update(id, article));
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        StpUtil.checkLogin();
        articleService.delete(id);
        return Result.success();
    }
    
    @GetMapping("/heatmap")
    public Result<Map<String, Integer>> getPublishHeatmap() {
        return Result.success(articleService.getPublishHeatmap());
    }
    
    /**
     * 获取网站公开统计数据
     * 包括文章数、总浏览量、总点赞数、总评论数
     */
    @GetMapping("/stats/public")
    public Result<Map<String, Object>> getPublicStats() {
        return Result.success(articleService.getPublicStats());
    }
    
    @GetMapping("/ranking")
    public Result<List<ArticleRankingDTO>> getRanking(
            @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(articleService.getTopArticles(limit));
    }
    
    /**
     * 获取精选文章
     * 优先返回置顶文章，不足时用浏览数最多的文章补充
     */
    @GetMapping("/featured")
    public Result<List<Article>> getFeatured(
            @RequestParam(defaultValue = "3") Integer limit) {
        return Result.success(articleService.getFeaturedArticles(limit));
    }
    
    /**
     * 获取归档文章列表
     * 支持搜索、标签筛选、排序
     */
    @GetMapping("/archive")
    public Result<List<Article>> getArchive(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<Integer> tagIds,
            @RequestParam(defaultValue = "date-desc") String sortBy) {
        return Result.success(articleService.getArchiveArticles(keyword, tagIds, sortBy));
    }
}

