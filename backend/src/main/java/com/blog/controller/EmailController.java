package com.blog.controller;

import com.blog.common.Result;
import com.blog.service.EmailService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {
    
    private final EmailService emailService;
    
    /**
     * 发送验证码
     */
    @PostMapping("/send-code")
    public Result<Void> sendCode(
            @RequestParam @NotBlank(message = "邮箱不能为空") 
            @Email(message = "邮箱格式不正确") String email) {
        emailService.sendVerificationCode(email);
        return Result.success();
    }
}
