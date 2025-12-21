package com.blog.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.blog.entity.User;
import com.blog.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SaTokenConfig implements WebMvcConfigurer, StpInterface {
    
    private final UserMapper userMapper;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 获取当前请求
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                // OPTIONS 请求直接放行（CORS 预检请求）
                if ("OPTIONS".equals(request.getMethod())) {
                    return;
                }
            }
            StpUtil.checkLogin();
        }))
                .addPathPatterns(
                        "/api/admin/**",          // 管理员接口
                        "/api/user/**",           // 用户个人信息接口
                        "/api/articles/my",       // 我的文章
                        "/api/upload/**"          // 文件上传
                )
                .excludePathPatterns(
                        "/api/auth/**",           // 认证相关接口
                        "/api/email/**"           // 邮件相关接口
                );
    }
    
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return new ArrayList<>();
    }
    
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> roles = new ArrayList<>();
        User user = userMapper.selectById(Integer.parseInt(loginId.toString()));
        if (user != null && user.getRole() == 1) {
            roles.add("admin");
        }
        return roles;
    }
}
