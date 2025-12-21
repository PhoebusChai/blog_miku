package com.blog.service;

import cn.hutool.core.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;
    
    @Value("${spring.mail.username}")
    private String from;
    
    private static final String CODE_PREFIX = "email:code:";
    private static final int CODE_LENGTH = 6;
    private static final int CODE_EXPIRE_MINUTES = 5;
    
    /**
     * 发送验证码(异步)
     */
    @Async
    public void sendVerificationCode(String email) {
        // 检查是否频繁发送
        String key = CODE_PREFIX + email;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            if (expire != null && expire > 240) { // 5分钟内只能发送一次
                throw new RuntimeException("验证码已发送，请稍后再试");
            }
        }
        
        // 生成6位数字验证码
        String code = RandomUtil.randomNumbers(CODE_LENGTH);
        
        // 发送邮件
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(email);
            message.setSubject("【个人博客】邮箱验证码");
            message.setText(buildEmailContent(code));
            
            mailSender.send(message);
            
            // 存储验证码到 Redis，有效期5分钟
            redisTemplate.opsForValue().set(key, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            
            log.info("验证码发送成功：{} -> {}", email, code);
        } catch (Exception e) {
            log.error("验证码发送失败：{}", email, e);
            throw new RuntimeException("验证码发送失败，请稍后重试");
        }
    }
    
    /**
     * 验证验证码
     */
    public boolean verifyCode(String email, String code) {
        String key = CODE_PREFIX + email;
        String savedCode = redisTemplate.opsForValue().get(key);
        
        if (savedCode == null) {
            return false;
        }
        
        if (savedCode.equals(code)) {
            // 验证成功后删除验证码
            redisTemplate.delete(key);
            return true;
        }
        
        return false;
    }
    
    /**
     * 构建邮件内容
     */
    private String buildEmailContent(String code) {
        return String.format(
            "您好！\n\n" +
            "您的验证码是：%s\n\n" +
            "验证码有效期为 %d 分钟，请尽快使用。\n\n" +
            "如果这不是您的操作，请忽略此邮件。\n\n" +
            "此邮件由系统自动发送，请勿回复。\n\n" +
            "——个人博客系统",
            code, CODE_EXPIRE_MINUTES
        );
    }
}
