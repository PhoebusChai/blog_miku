package com.blog.mapper;

import com.blog.entity.FriendLink;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FriendLinkMapper {

    @Insert("INSERT INTO friend_links (name, url, avatar, description, status, sort_order, created_at, updated_at) " +
            "VALUES (#{name}, #{url}, #{avatar}, #{description}, #{status}, #{sortOrder}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(FriendLink friendLink);

    @Select("SELECT id, name, url, avatar, description, status, sort_order as sortOrder, " +
            "created_at as createdAt, updated_at as updatedAt FROM friend_links WHERE id = #{id}")
    FriendLink selectById(Integer id);

    @Select("SELECT id, name, url, avatar, description, status, sort_order as sortOrder, " +
            "created_at as createdAt, updated_at as updatedAt FROM friend_links ORDER BY sort_order ASC, created_at DESC")
    List<FriendLink> selectAll();

    @Select("SELECT id, name, url, avatar, description, status, sort_order as sortOrder, " +
            "created_at as createdAt, updated_at as updatedAt FROM friend_links WHERE status = #{status} ORDER BY sort_order ASC, created_at DESC")
    List<FriendLink> selectByStatus(Integer status);

    @Select("SELECT id, name, url, avatar, description, status, sort_order as sortOrder, " +
            "created_at as createdAt, updated_at as updatedAt FROM friend_links WHERE status = 1 ORDER BY sort_order ASC, created_at DESC")
    List<FriendLink> selectApproved();

    @Update("UPDATE friend_links SET name = #{name}, url = #{url}, avatar = #{avatar}, description = #{description}, " +
            "status = #{status}, sort_order = #{sortOrder}, updated_at = NOW() WHERE id = #{id}")
    int updateById(FriendLink friendLink);

    @Update("UPDATE friend_links SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    @Delete("DELETE FROM friend_links WHERE id = #{id}")
    int deleteById(Integer id);

    @Select("SELECT COUNT(*) FROM friend_links")
    Long count();

    @Select("SELECT COUNT(*) FROM friend_links WHERE status = #{status}")
    Long countByStatus(Integer status);
}
