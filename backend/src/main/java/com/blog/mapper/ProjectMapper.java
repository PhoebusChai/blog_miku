package com.blog.mapper;

import com.blog.entity.Project;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProjectMapper {

    @Insert("INSERT INTO projects (title, description, cover_image, demo_url, github_url, tech_stack, status, sort_order, view_count, created_at, updated_at) " +
            "VALUES (#{title}, #{description}, #{coverImage}, #{demoUrl}, #{githubUrl}, #{techStack}, #{status}, #{sortOrder}, #{viewCount}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Project project);

    @Select("SELECT id, title, description, cover_image as coverImage, demo_url as demoUrl, github_url as githubUrl, " +
            "tech_stack as techStack, status, sort_order as sortOrder, view_count as viewCount, " +
            "created_at as createdAt, updated_at as updatedAt FROM projects WHERE id = #{id}")
    Project selectById(Integer id);

    @Select("SELECT id, title, description, cover_image as coverImage, demo_url as demoUrl, github_url as githubUrl, " +
            "tech_stack as techStack, status, sort_order as sortOrder, view_count as viewCount, " +
            "created_at as createdAt, updated_at as updatedAt FROM projects ORDER BY sort_order ASC, created_at DESC")
    List<Project> selectAll();

    @Select("SELECT id, title, description, cover_image as coverImage, demo_url as demoUrl, github_url as githubUrl, " +
            "tech_stack as techStack, status, sort_order as sortOrder, view_count as viewCount, " +
            "created_at as createdAt, updated_at as updatedAt FROM projects WHERE status = #{status} ORDER BY sort_order ASC, created_at DESC")
    List<Project> selectByStatus(Integer status);

    @Update("UPDATE projects SET title = #{title}, description = #{description}, cover_image = #{coverImage}, " +
            "demo_url = #{demoUrl}, github_url = #{githubUrl}, tech_stack = #{techStack}, status = #{status}, " +
            "sort_order = #{sortOrder}, updated_at = NOW() WHERE id = #{id}")
    int updateById(Project project);

    @Update("UPDATE projects SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(Integer id);

    @Delete("DELETE FROM projects WHERE id = #{id}")
    int deleteById(Integer id);

    @Select("SELECT COUNT(*) FROM projects")
    Long count();

    @Select("SELECT COUNT(*) FROM projects WHERE status = #{status}")
    Long countByStatus(Integer status);
}
