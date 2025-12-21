package com.blog.mapper;

import com.blog.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    
    @Select("SELECT * FROM users WHERE id = #{id}")
    User selectById(Integer id);
    
    @Select("SELECT * FROM users WHERE email = #{email}")
    User selectByEmail(String email);
    
    @Insert("INSERT INTO users(email, password, name, avatar, role) " +
            "VALUES(#{email}, #{password}, #{name}, #{avatar}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    @Update("UPDATE users SET name=#{name}, avatar=#{avatar}, " +
            "updated_at=NOW() WHERE id=#{id}")
    int update(User user);
    
    @Update("UPDATE users SET last_login_at=NOW() WHERE id=#{id}")
    int updateLastLoginTime(Integer id);
}
