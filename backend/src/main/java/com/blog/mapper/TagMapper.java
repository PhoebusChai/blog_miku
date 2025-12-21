package com.blog.mapper;

import com.blog.entity.Tag;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface TagMapper {
    
    @Select("SELECT * FROM tags WHERE id = #{id}")
    Tag selectById(Integer id);
    
    @Select("SELECT * FROM tags ORDER BY article_count DESC, created_at DESC")
    List<Tag> selectAll();
    
    @Select("SELECT t.* FROM tags t " +
            "INNER JOIN article_tags at ON t.id = at.tag_id " +
            "WHERE at.article_id = #{articleId}")
    List<Tag> selectByArticleId(Integer articleId);
    
    @Insert("INSERT INTO tags(name, slug) VALUES(#{name}, #{slug})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Tag tag);
    
    @Update("UPDATE tags SET name=#{name}, slug=#{slug}, updated_at=NOW() WHERE id=#{id}")
    int update(Tag tag);
    
    @Delete("DELETE FROM tags WHERE id = #{id}")
    int deleteById(Integer id);
    
    @Update("UPDATE tags SET article_count = article_count + 1 WHERE id = #{id}")
    int incrementArticleCount(Integer id);
    
    @Update("UPDATE tags SET article_count = article_count - 1 WHERE id = #{id} AND article_count > 0")
    int decrementArticleCount(Integer id);
}
