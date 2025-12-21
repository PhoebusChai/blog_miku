package com.blog.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.blog.common.Result;
import com.blog.entity.Tag;
import com.blog.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {
    
    private final TagService tagService;
    
    @GetMapping
    public Result<List<Tag>> list() {
        return Result.success(tagService.getAllTags());
    }
    
    @GetMapping("/{id}")
    public Result<Tag> getById(@PathVariable Integer id) {
        return Result.success(tagService.getById(id));
    }
    
    @PostMapping
    @SaCheckRole("admin")
    public Result<Tag> create(@RequestBody Tag tag) {
        return Result.success(tagService.create(tag));
    }
    
    @PutMapping("/{id}")
    @SaCheckRole("admin")
    public Result<Tag> update(@PathVariable Integer id, @RequestBody Tag tag) {
        return Result.success(tagService.update(id, tag));
    }
    
    @DeleteMapping("/{id}")
    @SaCheckRole("admin")
    public Result<Void> delete(@PathVariable Integer id) {
        tagService.delete(id);
        return Result.success();
    }
}
