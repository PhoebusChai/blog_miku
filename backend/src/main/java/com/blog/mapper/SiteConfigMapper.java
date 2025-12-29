package com.blog.mapper;

import com.blog.entity.SiteConfig;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SiteConfigMapper {
    
    @Select("SELECT * FROM site_config ORDER BY id")
    List<SiteConfig> findAll();
    
    @Select("SELECT * FROM site_config WHERE config_key = #{key}")
    SiteConfig findByKey(String key);
    
    @Insert("INSERT INTO site_config (config_key, config_value, config_type, description) " +
            "VALUES (#{configKey}, #{configValue}, #{configType}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SiteConfig config);
    
    @Update("UPDATE site_config SET config_value = #{configValue}, " +
            "config_type = #{configType}, description = #{description} " +
            "WHERE config_key = #{configKey}")
    int updateByKey(SiteConfig config);
    
    @Delete("DELETE FROM site_config WHERE config_key = #{key}")
    int deleteByKey(String key);
    
    @Select("SELECT COUNT(*) FROM site_config WHERE config_key = #{key}")
    int existsByKey(String key);
}
