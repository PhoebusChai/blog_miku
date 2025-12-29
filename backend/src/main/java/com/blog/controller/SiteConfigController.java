package com.blog.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.blog.service.SiteConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class SiteConfigController {
    
    @Autowired
    private SiteConfigService siteConfigService;
    
    /**
     * 获取所有公开配置（无需登录）
     */
    @GetMapping("/public")
    public ResponseEntity<Map<String, Object>> getPublicConfigs() {
        Map<String, Object> configs = siteConfigService.getAllConfigs();
        return ResponseEntity.ok(configs);
    }
    
    /**
     * 获取所有配置（管理员）
     */
    @GetMapping
    @SaCheckRole("admin")
    public ResponseEntity<Map<String, Object>> getAllConfigs() {
        Map<String, Object> configs = siteConfigService.getAllConfigs();
        return ResponseEntity.ok(configs);
    }
    
    /**
     * 获取单个配置
     */
    @GetMapping("/{key}")
    public ResponseEntity<Object> getConfig(@PathVariable String key) {
        Object value = siteConfigService.getConfig(key);
        return ResponseEntity.ok(value);
    }
    
    /**
     * 批量保存配置（管理员）
     */
    @PostMapping
    @SaCheckRole("admin")
    public ResponseEntity<Void> saveConfigs(@RequestBody Map<String, Object> configs) {
        siteConfigService.saveConfigs(configs);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 保存单个配置（管理员）
     */
    @PutMapping("/{key}")
    @SaCheckRole("admin")
    public ResponseEntity<Void> saveConfig(@PathVariable String key, @RequestBody Object value) {
        siteConfigService.saveConfig(key, value);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 删除配置（管理员）
     */
    @DeleteMapping("/{key}")
    @SaCheckRole("admin")
    public ResponseEntity<Void> deleteConfig(@PathVariable String key) {
        siteConfigService.deleteConfig(key);
        return ResponseEntity.ok().build();
    }
}
