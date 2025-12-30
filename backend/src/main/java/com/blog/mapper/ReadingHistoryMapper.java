package com.blog.mapper;

import com.blog.entity.ReadingHistory;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReadingHistoryMapper {
    
    /**
     * 查询用户对文章的阅读记录
     */
    @Select("SELECT * FROM reading_history WHERE user_id = #{userId} AND article_id = #{articleId}")
    ReadingHistory selectByUserAndArticle(@Param("userId") Integer userId, 
                                          @Param("articleId") Integer articleId);
    
    /**
     * 获取用户的阅读记录列表
     */
    @Select("SELECT rh.*, a.title as article_title, a.summary as article_summary, a.cover_image as article_cover " +
            "FROM reading_history rh " +
            "LEFT JOIN articles a ON rh.article_id = a.id " +
            "WHERE rh.user_id = #{userId} AND a.status = 1 " +
            "ORDER BY rh.updated_at DESC LIMIT #{limit}")
    @Results({
        @Result(property = "articleTitle", column = "article_title"),
        @Result(property = "articleSummary", column = "article_summary"),
        @Result(property = "articleCover", column = "article_cover")
    })
    List<ReadingHistory> selectByUserId(@Param("userId") Integer userId, @Param("limit") Integer limit);
    
    /**
     * 统计用户点赞数
     */
    @Select("SELECT COUNT(*) FROM reading_history WHERE user_id = #{userId} AND is_liked = 1")
    int countLikesByUserId(Integer userId);
    
    /**
     * 统计用户收藏数
     */
    @Select("SELECT COUNT(*) FROM reading_history WHERE user_id = #{userId} AND is_bookmarked = 1")
    int countBookmarksByUserId(Integer userId);
    
    /**
     * 获取用户点赞的文章
     */
    @Select("SELECT rh.*, a.title as article_title, a.summary as article_summary, a.cover_image as article_cover " +
            "FROM reading_history rh " +
            "LEFT JOIN articles a ON rh.article_id = a.id " +
            "WHERE rh.user_id = #{userId} AND rh.is_liked = 1 AND a.status = 1 " +
            "ORDER BY rh.updated_at DESC")
    @Results({
        @Result(property = "articleTitle", column = "article_title"),
        @Result(property = "articleSummary", column = "article_summary"),
        @Result(property = "articleCover", column = "article_cover")
    })
    List<ReadingHistory> selectLikedByUserId(Integer userId);
    
    /**
     * 获取用户收藏的文章
     */
    @Select("SELECT rh.*, a.title as article_title, a.summary as article_summary, a.cover_image as article_cover " +
            "FROM reading_history rh " +
            "LEFT JOIN articles a ON rh.article_id = a.id " +
            "WHERE rh.user_id = #{userId} AND rh.is_bookmarked = 1 AND a.status = 1 " +
            "ORDER BY rh.updated_at DESC")
    @Results({
        @Result(property = "articleTitle", column = "article_title"),
        @Result(property = "articleSummary", column = "article_summary"),
        @Result(property = "articleCover", column = "article_cover")
    })
    List<ReadingHistory> selectBookmarkedByUserId(Integer userId);
    
    /**
     * 插入阅读记录
     */
    @Insert("INSERT INTO reading_history (user_id, article_id, is_liked, is_bookmarked, created_at, updated_at) " +
            "VALUES (#{userId}, #{articleId}, #{isLiked}, #{isBookmarked}, NOW(), NOW()) " +
            "ON DUPLICATE KEY UPDATE updated_at = NOW()")
    int insert(ReadingHistory readingHistory);
    
    /**
     * 更新点赞状态
     */
    @Update("UPDATE reading_history SET is_liked = #{isLiked}, updated_at = NOW() " +
            "WHERE user_id = #{userId} AND article_id = #{articleId}")
    int updateLikeStatus(@Param("userId") Integer userId, 
                        @Param("articleId") Integer articleId, 
                        @Param("isLiked") Integer isLiked);
    
    /**
     * 更新收藏状态
     */
    @Update("UPDATE reading_history SET is_bookmarked = #{isBookmarked}, updated_at = NOW() " +
            "WHERE user_id = #{userId} AND article_id = #{articleId}")
    int updateBookmarkStatus(@Param("userId") Integer userId, 
                            @Param("articleId") Integer articleId, 
                            @Param("isBookmarked") Integer isBookmarked);
    
    /**
     * 插入或更新阅读记录（用于记录阅读）
     */
    @Insert("INSERT INTO reading_history (user_id, article_id, is_liked, is_bookmarked, created_at, updated_at) " +
            "VALUES (#{userId}, #{articleId}, 0, 0, NOW(), NOW()) " +
            "ON DUPLICATE KEY UPDATE updated_at = NOW()")
    int insertOrUpdate(@Param("userId") Integer userId, @Param("articleId") Integer articleId);
}
