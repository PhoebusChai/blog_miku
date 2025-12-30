package com.blog.service;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.blog.dto.LoginDTO;
import com.blog.dto.RegisterDTO;
import com.blog.dto.RegisterWithCodeDTO;
import com.blog.entity.User;
import com.blog.mapper.UserMapper;
import com.blog.mapper.CommentMapper;
import com.blog.mapper.ReadingHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final EmailService emailService;
    private final CommentMapper commentMapper;
    private final ReadingHistoryMapper readingHistoryMapper;

    public Map<String, Object> login(LoginDTO dto) {
        User user = userMapper.selectByEmail(dto.getEmail());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        // 登录
        StpUtil.login(user.getId());
        userMapper.updateLastLoginTime(user.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("token", StpUtil.getTokenValue());
        result.put("user", user);
        return result;
    }

    public void register(RegisterDTO dto) {
        if (userMapper.selectByEmail(dto.getEmail()) != null) {
            throw new RuntimeException("邮箱已被注册");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setName(dto.getName());
        user.setAvatar("https://api.dicebear.com/7.x/avataaars/svg?seed=" + dto.getName());
        user.setRole(0);
        user.setStatus(1);

        userMapper.insert(user);
    }

    public void registerWithCode(RegisterWithCodeDTO dto) {
        // 验证验证码
        if (!emailService.verifyCode(dto.getEmail(), dto.getCode())) {
            throw new RuntimeException("验证码错误或已过期");
        }

        if (userMapper.selectByEmail(dto.getEmail()) != null) {
            throw new RuntimeException("邮箱已被注册");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setName(dto.getName());
        user.setAvatar("https://api.dicebear.com/7.x/avataaars/svg?seed=" + dto.getName());
        user.setRole(0);
        user.setStatus(1);

        userMapper.insert(user);
    }

    public void logout() {
        StpUtil.logout();
    }

    public User getCurrentUser() {
        int userId = StpUtil.getLoginIdAsInt();
        return userMapper.selectById(userId);
    }

    public User getUserById(Integer id) {
        return userMapper.selectById(id);
    }

    // ========== 个人中心功能 ==========

    public User updateProfile(Integer userId, String name, String avatar) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (StrUtil.isNotBlank(name)) {
            user.setName(name);
        }
        if (StrUtil.isNotBlank(avatar)) {
            user.setAvatar(avatar);
        }
        userMapper.update(user);
        return userMapper.selectById(userId);
    }

    public void changePassword(Integer userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        if (StrUtil.isBlank(newPassword) || newPassword.length() < 6) {
            throw new RuntimeException("新密码长度不能少于6位");
        }
        userMapper.updatePassword(userId, BCrypt.hashpw(newPassword));
    }

    public Map<String, Object> getUserActivity(Integer userId) {
        Map<String, Object> activity = new HashMap<>();
        activity.put("commentCount", commentMapper.countByUserId(userId));
        activity.put("likeCount", readingHistoryMapper.countLikesByUserId(userId));
        activity.put("bookmarkCount", readingHistoryMapper.countBookmarksByUserId(userId));
        return activity;
    }

    // ========== 管理员功能 ==========

    public List<User> getAllUsers() {
        return userMapper.selectAll();
    }

    public List<User> getUsersByRole(Integer role) {
        return userMapper.selectByRole(role);
    }

    public List<User> getUsersByStatus(Integer status) {
        return userMapper.selectByStatus(status);
    }

    public User createUser(User user) {
        if (userMapper.selectByEmail(user.getEmail()) != null) {
            throw new RuntimeException("邮箱已被注册");
        }
        user.setPassword(BCrypt.hashpw(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole(0);
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        if (user.getAvatar() == null || user.getAvatar().isEmpty()) {
            user.setAvatar("https://api.dicebear.com/7.x/avataaars/svg?seed=" + user.getName());
        }
        userMapper.insert(user);
        return user;
    }

    public void updateUserByAdmin(User user) {
        userMapper.updateByAdmin(user);
    }

    public void updateUserStatus(Integer id, Integer status) {
        userMapper.updateStatus(id, status);
    }

    public void deleteUser(Integer id) {
        userMapper.deleteById(id);
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", userMapper.count());
        stats.put("admins", userMapper.countByRole(1));
        stats.put("users", userMapper.countByRole(0));
        stats.put("active", userMapper.countByStatus(1));
        stats.put("disabled", userMapper.countByStatus(0));
        return stats;
    }
}
