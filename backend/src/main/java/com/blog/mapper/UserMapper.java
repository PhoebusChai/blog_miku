package com.blog.mapper;

import com.blog.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM users WHERE id = #{id}")
    User selectById(Integer id);

    @Select("SELECT * FROM users WHERE email = #{email}")
    User selectByEmail(String email);

    @Select("SELECT * FROM users ORDER BY created_at DESC")
    List<User> selectAll();

    @Select("SELECT * FROM users WHERE role = #{role} ORDER BY created_at DESC")
    List<User> selectByRole(Integer role);

    @Select("SELECT * FROM users WHERE status = #{status} ORDER BY created_at DESC")
    List<User> selectByStatus(Integer status);

    @Insert("INSERT INTO users(email, password, name, avatar, role, status) " +
            "VALUES(#{email}, #{password}, #{name}, #{avatar}, #{role}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE users SET name=#{name}, avatar=#{avatar}, " +
            "updated_at=NOW() WHERE id=#{id}")
    int update(User user);

    @Update("UPDATE users SET name=#{name}, avatar=#{avatar}, role=#{role}, status=#{status}, " +
            "updated_at=NOW() WHERE id=#{id}")
    int updateByAdmin(User user);

    @Update("UPDATE users SET status=#{status}, updated_at=NOW() WHERE id=#{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    @Update("UPDATE users SET last_login_at=NOW() WHERE id=#{id}")
    int updateLastLoginTime(Integer id);

    @Delete("DELETE FROM users WHERE id = #{id}")
    int deleteById(Integer id);

    @Select("SELECT COUNT(*) FROM users")
    Long count();

    @Select("SELECT COUNT(*) FROM users WHERE role = #{role}")
    Long countByRole(Integer role);

    @Select("SELECT COUNT(*) FROM users WHERE status = #{status}")
    Long countByStatus(Integer status);
}
