package com.blog.mapper;

import com.blog.entity.ReadingHistory;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ReadingHistoryMapper {
    
    /**
     * 查询用户对文章的阅读记录
     */
    @Select("SELECT * FROM reading_history WHERE user_id = #{userId} AND article_id = #{articleId}")
    ReadingHistory selectByUserAndArticle(@Param("userId") Integer userId, 
                                          @Param("articleId") Integer articleId);
    
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
