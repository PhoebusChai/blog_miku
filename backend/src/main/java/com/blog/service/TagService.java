package com.blog.service;

import cn.hutool.core.util.StrUtil;
import com.blog.entity.Tag;
import com.blog.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
    
    private final TagMapper tagMapper;
    
    public List<Tag> getAllTags() {
        return tagMapper.selectAll();
    }
    
    public Tag getById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("标签ID无效");
        }
        
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new RuntimeException("标签不存在");
        }
        return tag;
    }
    
    @Transactional
    public Tag create(Tag tag) {
        // 验证标签名称
        if (StrUtil.isBlank(tag.getName())) {
            throw new IllegalArgumentException("标签名称不能为空");
        }
        
        if (tag.getName().length() > 50) {
            throw new IllegalArgumentException("标签名称不能超过50个字符");
        }
        
        // 生成 slug
        if (StrUtil.isBlank(tag.getSlug())) {
            tag.setSlug(generateSlug(tag.getName()));
        } else {
            // 验证 slug 格式
            if (!tag.getSlug().matches("^[a-z0-9-]+$")) {
                throw new IllegalArgumentException("标签别名只能包含小写字母、数字和连字符");
            }
        }
        
        if (tag.getSlug().length() > 50) {
            throw new IllegalArgumentException("标签别名不能超过50个字符");
        }
        
        try {
            tagMapper.insert(tag);
            return tag;
        } catch (Exception e) {
            if (e.getMessage().contains("Duplicate entry")) {
                if (e.getMessage().contains("name")) {
                    throw new RuntimeException("标签名称已存在");
                } else if (e.getMessage().contains("slug")) {
                    throw new RuntimeException("标签别名已存在");
                }
            }
            throw new RuntimeException("创建标签失败：" + e.getMessage());
        }
    }
    
    @Transactional
    public Tag update(Integer id, Tag tag) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("标签ID无效");
        }
        
        Tag existing = tagMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("标签不存在");
        }
        
        // 验证标签名称
        if (StrUtil.isBlank(tag.getName())) {
            throw new IllegalArgumentException("标签名称不能为空");
        }
        
        if (tag.getName().length() > 50) {
            throw new IllegalArgumentException("标签名称不能超过50个字符");
        }
        
        tag.setId(id);
        
        // 更新 slug
        if (StrUtil.isBlank(tag.getSlug())) {
            tag.setSlug(generateSlug(tag.getName()));
        } else {
            // 验证 slug 格式
            if (!tag.getSlug().matches("^[a-z0-9-]+$")) {
                throw new IllegalArgumentException("标签别名只能包含小写字母、数字和连字符");
            }
        }
        
        if (tag.getSlug().length() > 50) {
            throw new IllegalArgumentException("标签别名不能超过50个字符");
        }
        
        try {
            tagMapper.update(tag);
            return tagMapper.selectById(id);
        } catch (Exception e) {
            if (e.getMessage().contains("Duplicate entry")) {
                if (e.getMessage().contains("name")) {
                    throw new RuntimeException("标签名称已存在");
                } else if (e.getMessage().contains("slug")) {
                    throw new RuntimeException("标签别名已存在");
                }
            }
            throw new RuntimeException("更新标签失败：" + e.getMessage());
        }
    }
    
    @Transactional
    public void delete(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("标签ID无效");
        }
        
        Tag existing = tagMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("标签不存在");
        }
        
        if (existing.getArticleCount() != null && existing.getArticleCount() > 0) {
            throw new RuntimeException("该标签下还有 " + existing.getArticleCount() + " 篇文章，无法删除");
        }
        
        try {
            tagMapper.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("删除标签失败：" + e.getMessage());
        }
    }
    
    /**
     * 生成URL友好的slug
     */
    private String generateSlug(String name) {
        if (StrUtil.isBlank(name)) {
            return "";
        }
        
        // 转换为小写
        String slug = name.toLowerCase();
        
        // 替换空格和特殊字符为连字符
        slug = slug.replaceAll("[\\s_]+", "-");
        
        // 移除非字母数字和连字符的字符
        slug = slug.replaceAll("[^a-z0-9\\u4e00-\\u9fa5-]+", "");
        
        // 移除首尾的连字符
        slug = slug.replaceAll("^-+|-+$", "");
        
        // 如果slug为空，使用时间戳
        if (StrUtil.isBlank(slug)) {
            slug = "tag-" + System.currentTimeMillis();
        }
        
        return slug;
    }
}
