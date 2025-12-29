package com.blog.service;

import com.blog.entity.SiteConfig;
import com.blog.mapper.SiteConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SiteConfigService {
    
    @Autowired
    private SiteConfigMapper siteConfigMapper;
    
    /**
     * 获取所有配置（以Map形式返回）
     */
    public Map<String, Object> getAllConfigs() {
        List<SiteConfig> configs = siteConfigMapper.findAll();
        Map<String, Object> result = new HashMap<>();
        
        for (SiteConfig config : configs) {
            result.put(config.getConfigKey(), parseValue(config));
        }
        
        return result;
    }
    
    /**
     * 获取单个配置值
     */
    public Object getConfig(String key) {
        SiteConfig config = siteConfigMapper.findByKey(key);
        if (config == null) {
            return null;
        }
        return parseValue(config);
    }
    
    /**
     * 获取字符串配置
     */
    public String getStringConfig(String key, String defaultValue) {
        SiteConfig config = siteConfigMapper.findByKey(key);
        if (config == null || config.getConfigValue() == null) {
            return defaultValue;
        }
        return config.getConfigValue();
    }
    
    /**
     * 批量保存配置
     */
    public void saveConfigs(Map<String, Object> configs) {
        for (Map.Entry<String, Object> entry : configs.entrySet()) {
            saveConfig(entry.getKey(), entry.getValue());
        }
    }
    
    /**
     * 保存单个配置
     */
    public void saveConfig(String key, Object value) {
        SiteConfig config = new SiteConfig();
        config.setConfigKey(key);
        config.setConfigValue(value != null ? value.toString() : null);
        config.setConfigType(getConfigType(value));
        
        if (siteConfigMapper.existsByKey(key) > 0) {
            siteConfigMapper.updateByKey(config);
        } else {
            siteConfigMapper.insert(config);
        }
    }
    
    /**
     * 删除配置
     */
    public void deleteConfig(String key) {
        siteConfigMapper.deleteByKey(key);
    }
    
    /**
     * 根据配置类型解析值
     */
    private Object parseValue(SiteConfig config) {
        if (config.getConfigValue() == null) {
            return null;
        }
        
        Integer type = config.getConfigType();
        if (type == null) {
            type = 0;
        }
        
        switch (type) {
            case 1: // 数字
                try {
                    return Integer.parseInt(config.getConfigValue());
                } catch (NumberFormatException e) {
                    return config.getConfigValue();
                }
            case 2: // 布尔值
                return "true".equalsIgnoreCase(config.getConfigValue());
            case 3: // JSON（返回原始字符串，由前端解析）
                return config.getConfigValue();
            default: // 字符串
                return config.getConfigValue();
        }
    }
    
    /**
     * 根据值类型获取配置类型
     */
    private Integer getConfigType(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return 1;
        }
        if (value instanceof Boolean) {
            return 2;
        }
        String str = value.toString();
        if (str.startsWith("{") || str.startsWith("[")) {
            return 3;
        }
        return 0;
    }
}
