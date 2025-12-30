package com.blog.mapper;

import com.blog.entity.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommentMapper {
    
    /**
     * 插入评论
     */
    @Insert("INSERT INTO comments (article_id, user_id, parent_id, guest_name, guest_email, content, ip_address, like_count, status, created_at, updated_at) " +
            "VALUES (#{articleId}, #{userId}, #{parentId}, #{guestName}, #{guestEmail}, #{content}, #{ipAddress}, #{likeCount}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Comment comment);
    
    /**
     * 根据ID查询评论
     */
    @Select("SELECT * FROM comments WHERE id = #{id}")
    Comment selectById(Integer id);
    
    /**
     * 更新评论
     */
    @Update("UPDATE comments SET like_count = #{likeCount}, status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateById(Comment comment);
    
    /**
     * 删除评论
     */
    @Delete("DELETE FROM comments WHERE id = #{id}")
    int deleteById(Integer id);
    
    /**
     * 根据父评论ID查询子评论
     */
    @Select("SELECT * FROM comments WHERE parent_id = #{parentId}")
    List<Comment> selectByParentId(Integer parentId);
    
    /**
     * 获取文章评论列表（包含用户信息）
     * 使用 DATE_FORMAT 确保日期格式正确
     */
    @Select("SELECT c.id, c.article_id as articleId, c.user_id as userId, c.parent_id as parentId, " +
            "c.guest_name as guestName, c.guest_email as guestEmail, " +
            "c.content, c.ip_address as ipAddress, c.like_count as likeCount, c.status, " +
            "DATE_FORMAT(c.created_at, '%Y-%m-%dT%H:%i:%s') as createdAt, " +
            "DATE_FORMAT(c.updated_at, '%Y-%m-%dT%H:%i:%s') as updatedAt, " +
            "u.name as userName, u.avatar as userAvatar " +
            "FROM comments c " +
            "LEFT JOIN users u ON c.user_id = u.id " +
            "WHERE c.article_id = #{articleId} AND c.status = 1 " +
            "ORDER BY c.created_at DESC")
    List<Map<String, Object>> getArticleComments(Integer articleId);
    
    /**
     * 获取留言板评论列表（包含用户信息）
     * 使用 DATE_FORMAT 确保日期格式正确
     */
    @Select("SELECT c.id, c.article_id as articleId, c.user_id as userId, c.parent_id as parentId, " +
            "c.guest_name as guestName, c.guest_email as guestEmail, " +
            "c.content, c.ip_address as ipAddress, c.like_count as likeCount, c.status, " +
            "DATE_FORMAT(c.created_at, '%Y-%m-%dT%H:%i:%s') as createdAt, " +
            "DATE_FORMAT(c.updated_at, '%Y-%m-%dT%H:%i:%s') as updatedAt, " +
            "u.name as userName, u.avatar as userAvatar " +
            "FROM comments c " +
            "LEFT JOIN users u ON c.user_id = u.id " +
            "WHERE c.article_id IS NULL AND c.status = 1 " +
            "ORDER BY c.created_at DESC")
    List<Map<String, Object>> getGuestbookComments();
    
    /**
     * 获取所有评论（管理员）
     * 支持分页、状态筛选、文章筛选、搜索
     */
    List<Map<String, Object>> getAdminComments(@Param("offset") Integer offset,
                                               @Param("pageSize") Integer pageSize,
                                               @Param("status") Integer status,
                                               @Param("articleId") Integer articleId,
                                               @Param("keyword") String keyword);
    
    /**
     * 统计评论数量（管理员）
     */
    Long countAdminComments(@Param("status") Integer status,
                           @Param("articleId") Integer articleId,
                           @Param("keyword") String keyword);
    
    /**
     * 按状态统计评论数量
     */
    @Select({
        "<script>",
        "SELECT COUNT(*) FROM comments",
        "<if test='status != null'>",
        "WHERE status = #{status}",
        "</if>",
        "</script>"
    })
    Long countByStatus(@Param("status") Integer status);
    
    /**
     * 统计用户的评论数量
     */
    @Select("SELECT COUNT(*) FROM comments WHERE user_id = #{userId} AND status = 1")
    int countByUserId(Integer userId);
}
