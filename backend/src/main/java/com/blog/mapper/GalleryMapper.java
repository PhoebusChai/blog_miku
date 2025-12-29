package com.blog.mapper;

import com.blog.entity.Gallery;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GalleryMapper {

    @Insert("INSERT INTO gallery (title, description, image_url, thumbnail_url, category, sort_order, status, created_at, updated_at) " +
            "VALUES (#{title}, #{description}, #{imageUrl}, #{thumbnailUrl}, #{category}, #{sortOrder}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Gallery gallery);

    @Select("SELECT id, title, description, image_url as imageUrl, thumbnail_url as thumbnailUrl, " +
            "category, sort_order as sortOrder, status, created_at as createdAt, updated_at as updatedAt " +
            "FROM gallery WHERE id = #{id}")
    Gallery selectById(Integer id);

    @Select("SELECT id, title, description, image_url as imageUrl, thumbnail_url as thumbnailUrl, " +
            "category, sort_order as sortOrder, status, created_at as createdAt, updated_at as updatedAt " +
            "FROM gallery WHERE status != 2 ORDER BY sort_order ASC, created_at DESC")
    List<Gallery> selectAll();

    @Select("SELECT id, title, description, image_url as imageUrl, thumbnail_url as thumbnailUrl, " +
            "category, sort_order as sortOrder, status, created_at as createdAt, updated_at as updatedAt " +
            "FROM gallery WHERE category = #{category} AND status != 2 ORDER BY sort_order ASC, created_at DESC")
    List<Gallery> selectByCategory(String category);

    @Select("SELECT id, title, description, image_url as imageUrl, thumbnail_url as thumbnailUrl, " +
            "category, sort_order as sortOrder, status, created_at as createdAt, updated_at as updatedAt " +
            "FROM gallery WHERE status = 1 ORDER BY sort_order ASC, created_at DESC")
    List<Gallery> selectVisible();

    @Update("UPDATE gallery SET title = #{title}, description = #{description}, category = #{category}, " +
            "sort_order = #{sortOrder}, status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateById(Gallery gallery);

    @Delete("DELETE FROM gallery WHERE id = #{id}")
    int deleteById(Integer id);

    @Select("SELECT DISTINCT category FROM gallery WHERE category IS NOT NULL AND category != '' AND status != 2")
    List<String> selectAllCategories();

    @Select("SELECT COUNT(*) FROM gallery WHERE status != 2")
    Long count();
}
