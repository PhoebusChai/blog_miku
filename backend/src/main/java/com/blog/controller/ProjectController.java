package com.blog.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.blog.common.Result;
import com.blog.entity.Project;
import com.blog.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    /**
     * 获取所有项目（公开）
     */
    @GetMapping
    public Result<List<Project>> getAll(@RequestParam(required = false) Integer status) {
        List<Project> projects;
        if (status != null) {
            projects = projectService.getByStatus(status);
        } else {
            projects = projectService.getAll();
        }
        return Result.success(projects);
    }

    /**
     * 获取项目详情
     */
    @GetMapping("/{id}")
    public Result<Project> getById(@PathVariable Integer id) {
        Project project = projectService.getById(id);
        if (project == null) {
            return Result.error("项目不存在");
        }
        // 增加浏览量
        projectService.incrementViewCount(id);
        return Result.success(project);
    }

    /**
     * 获取项目统计（管理员）
     */
    @GetMapping("/admin/stats")
    public Result<Map<String, Object>> getStats() {
        StpUtil.checkRole("admin");
        return Result.success(projectService.getStats());
    }

    /**
     * 创建项目（管理员）
     */
    @PostMapping
    public Result<Project> create(@RequestBody Project project) {
        StpUtil.checkRole("admin");
        return Result.success(projectService.create(project));
    }

    /**
     * 更新项目（管理员）
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @RequestBody Project project) {
        StpUtil.checkRole("admin");
        project.setId(id);
        projectService.update(project);
        return Result.success();
    }

    /**
     * 删除项目（管理员）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        StpUtil.checkRole("admin");
        projectService.delete(id);
        return Result.success();
    }
}
