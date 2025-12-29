package com.blog.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.blog.common.Result;
import com.blog.entity.FriendLink;
import com.blog.service.FriendLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/links")
public class FriendLinkController {

    @Autowired
    private FriendLinkService friendLinkService;

    /**
     * 获取已通过的友链（公开）
     */
    @GetMapping
    public Result<List<FriendLink>> getApproved() {
        return Result.success(friendLinkService.getApproved());
    }

    /**
     * 获取所有友链（管理员）
     */
    @GetMapping("/admin/list")
    public Result<List<FriendLink>> getAll(@RequestParam(required = false) Integer status) {
        StpUtil.checkRole("admin");
        List<FriendLink> links;
        if (status != null) {
            links = friendLinkService.getByStatus(status);
        } else {
            links = friendLinkService.getAll();
        }
        return Result.success(links);
    }

    /**
     * 获取友链详情
     */
    @GetMapping("/{id}")
    public Result<FriendLink> getById(@PathVariable Integer id) {
        FriendLink link = friendLinkService.getById(id);
        if (link == null) {
            return Result.error("友链不存在");
        }
        return Result.success(link);
    }

    /**
     * 获取友链统计（管理员）
     */
    @GetMapping("/admin/stats")
    public Result<Map<String, Object>> getStats() {
        StpUtil.checkRole("admin");
        return Result.success(friendLinkService.getStats());
    }

    /**
     * 创建友链（管理员）
     */
    @PostMapping
    public Result<FriendLink> create(@RequestBody FriendLink friendLink) {
        StpUtil.checkRole("admin");
        return Result.success(friendLinkService.create(friendLink));
    }

    /**
     * 申请友链（公开，需要审核）
     */
    @PostMapping("/apply")
    public Result<FriendLink> apply(@RequestBody FriendLink friendLink) {
        friendLink.setStatus(0); // 待审核
        return Result.success(friendLinkService.create(friendLink));
    }

    /**
     * 更新友链（管理员）
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @RequestBody FriendLink friendLink) {
        StpUtil.checkRole("admin");
        friendLink.setId(id);
        friendLinkService.update(friendLink);
        return Result.success();
    }

    /**
     * 审核通过友链（管理员）
     */
    @PutMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Integer id) {
        StpUtil.checkRole("admin");
        friendLinkService.updateStatus(id, 1);
        return Result.success();
    }

    /**
     * 拒绝友链（管理员）
     */
    @PutMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Integer id) {
        StpUtil.checkRole("admin");
        friendLinkService.updateStatus(id, 2);
        return Result.success();
    }

    /**
     * 删除友链（管理员）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        StpUtil.checkRole("admin");
        friendLinkService.delete(id);
        return Result.success();
    }
}
