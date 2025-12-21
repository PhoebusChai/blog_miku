package com.blog.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.blog.common.Result;
import com.blog.dto.LoginDTO;
import com.blog.dto.RegisterDTO;
import com.blog.dto.RegisterWithCodeDTO;
import com.blog.entity.User;
import com.blog.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(userService.login(dto));
    }
    
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }
    
    @PostMapping("/register-with-code")
    public Result<Void> registerWithCode(@Valid @RequestBody RegisterWithCodeDTO dto) {
        userService.registerWithCode(dto);
        return Result.success();
    }
    
    @PostMapping("/logout")
    public Result<Void> logout() {
        userService.logout();
        return Result.success();
    }
    
    @GetMapping("/current")
    public Result<User> getCurrentUser() {
        if (!StpUtil.isLogin()) {
            return Result.error(401, "未登录");
        }
        return Result.success(userService.getCurrentUser());
    }
}
