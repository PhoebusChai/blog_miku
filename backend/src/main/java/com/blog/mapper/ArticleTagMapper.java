package com.blog.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface ArticleTagMapper {
    
    @Insert("INSERT INTO article_tags(article_id, tag_id) VALUES(#{articleId}, #{tagId})")
    int insert(@Param("articleId") Integer articleId, @Param("tagId") Integer tagId);
    
    @Delete("DELETE FROM article_tags WHERE article_id = #{articleId}")
    int deleteByArticleId(Integer articleId);
    
    @Delete("DELETE FROM article_tags WHERE article_id = #{articleId} AND tag_id = #{tagId}")
    int delete(@Param("articleId") Integer articleId, @Param("tagId") Integer tagId);
}
