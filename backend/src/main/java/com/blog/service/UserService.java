package com.blog.service;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.blog.dto.LoginDTO;
import com.blog.dto.RegisterDTO;
import com.blog.dto.RegisterWithCodeDTO;
import com.blog.entity.User;
import com.blog.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserMapper userMapper;
    private final EmailService emailService;
    
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
}
