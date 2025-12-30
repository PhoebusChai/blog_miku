package com.blog.mapper;

import com.blog.entity.Article;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface ArticleMapper {
    
    @Select("SELECT * FROM articles WHERE id = #{id}")
    Article selectById(Integer id);
    
    @Select("SELECT * FROM articles WHERE status = 1 " +
            "ORDER BY is_top DESC, published_at DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Article> selectPublishedList(@Param("offset") Integer offset, 
                                      @Param("limit") Integer limit);
    
    @Select("SELECT COUNT(*) FROM articles WHERE status = 1")
    Long countPublished();
    
    @Select("SELECT COALESCE(SUM(view_count), 0) FROM articles WHERE status = 1")
    Long sumViewCount();
    
    @Select("SELECT COALESCE(SUM(like_count), 0) FROM articles WHERE status = 1")
    Long sumLikeCount();
    
    @Select("SELECT COALESCE(SUM(comment_count), 0) FROM articles WHERE status = 1")
    Long sumCommentCount();
    
    @Select("SELECT * FROM articles WHERE author_id = #{authorId} " +
            "ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<Article> selectByAuthor(@Param("authorId") Integer authorId,
                                 @Param("offset") Integer offset,
                                 @Param("limit") Integer limit);
    
    @Insert("INSERT INTO articles(title, summary, content, cover_image, " +
            "author_id, status, published_at) " +
            "VALUES(#{title}, #{summary}, #{content}, #{coverImage}, " +
            "#{authorId}, #{status}, #{publishedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Article article);
    
    @Update("UPDATE articles SET title=#{title}, summary=#{summary}, " +
            "content=#{content}, cover_image=#{coverImage}, status=#{status}, " +
            "is_top=#{isTop}, updated_at=NOW() WHERE id=#{id}")
    int update(Article article);
    
    @Delete("DELETE FROM articles WHERE id = #{id}")
    int deleteById(Integer id);
    
    @Update("UPDATE articles SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(Integer id);
    
    @Update("UPDATE articles SET comment_count = comment_count + 1 WHERE id = #{id}")
    int incrementCommentCount(Integer id);
    
    @Update("UPDATE articles SET comment_count = GREATEST(comment_count - 1, 0) WHERE id = #{id}")
    int decrementCommentCount(Integer id);
    
    @Update("UPDATE articles SET like_count = like_count + 1 WHERE id = #{id}")
    int incrementLikeCount(Integer id);
    
    @Update("UPDATE articles SET like_count = GREATEST(like_count - 1, 0) WHERE id = #{id}")
    int decrementLikeCount(Integer id);
    
    @Select("SELECT DATE(published_at) as date, COUNT(*) as count " +
            "FROM articles " +
            "WHERE status = 1 AND published_at >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR) " +
            "GROUP BY DATE(published_at)")
    List<Map<String, Object>> selectPublishHeatmap();
    
    @Select("SELECT id, title, view_count " +
            "FROM articles " +
            "WHERE status = 1 " +
            "ORDER BY view_count DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> selectTopByViews(Integer limit);
    
    /**
     * 获取精选文章
     * 优先返回置顶文章，不足时用浏览数最多的文章补充
     */
    @Select("SELECT * FROM articles WHERE status = 1 " +
            "ORDER BY is_top DESC, view_count DESC, published_at DESC " +
            "LIMIT #{limit}")
    List<Article> selectFeaturedArticles(Integer limit);
    
    /**
     * 查询归档文章列表
     * 支持搜索、标签筛选、排序
     */
    List<Article> selectArchiveArticles(@Param("keyword") String keyword, 
                                       @Param("tagIds") List<Integer> tagIds, 
                                       @Param("sortBy") String sortBy);
}

