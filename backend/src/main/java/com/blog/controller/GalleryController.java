package com.blog.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.blog.common.Result;
import com.blog.entity.Gallery;
import com.blog.service.GalleryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gallery")
public class GalleryController {

    @Autowired
    private GalleryService galleryService;

    /**
     * 获取所有图片（公开，只返回可见的）
     */
    @GetMapping
    public Result<List<Gallery>> getVisible() {
        return Result.success(galleryService.getVisible());
    }

    /**
     * 获取所有图片（管理员）
     */
    @GetMapping("/admin/list")
    public Result<List<Gallery>> getAll(@RequestParam(required = false) String category) {
        StpUtil.checkRole("admin");
        List<Gallery> galleries;
        if (category != null && !category.isEmpty()) {
            galleries = galleryService.getByCategory(category);
        } else {
            galleries = galleryService.getAll();
        }
        return Result.success(galleries);
    }

    /**
     * 获取图片详情
     */
    @GetMapping("/{id}")
    public Result<Gallery> getById(@PathVariable Integer id) {
        Gallery gallery = galleryService.getById(id);
        if (gallery == null) {
            return Result.error("图片不存在");
        }
        return Result.success(gallery);
    }

    /**
     * 获取所有分类
     */
    @GetMapping("/categories")
    public Result<List<String>> getCategories() {
        return Result.success(galleryService.getAllCategories());
    }

    /**
     * 获取图片统计（管理员）
     */
    @GetMapping("/admin/stats")
    public Result<Map<String, Object>> getStats() {
        StpUtil.checkRole("admin");
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", galleryService.count());
        stats.put("categories", galleryService.getAllCategories().size());
        return Result.success(stats);
    }

    /**
     * 创建图片记录（管理员）
     */
    @PostMapping
    public Result<Gallery> create(@RequestBody Gallery gallery) {
        StpUtil.checkRole("admin");
        return Result.success(galleryService.create(gallery));
    }

    /**
     * 更新图片信息（管理员）
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @RequestBody Gallery gallery) {
        StpUtil.checkRole("admin");
        gallery.setId(id);
        galleryService.update(gallery);
        return Result.success();
    }

    /**
     * 删除图片（管理员）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        StpUtil.checkRole("admin");
        galleryService.delete(id);
        return Result.success();
    }
}
