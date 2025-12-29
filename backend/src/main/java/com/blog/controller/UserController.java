package com.blog.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.blog.common.Result;
import com.blog.entity.User;
import com.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取所有用户（管理员）
     */
    @GetMapping("/admin/list")
    public Result<List<User>> getAll(
            @RequestParam(required = false) Integer role,
            @RequestParam(required = false) Integer status) {
        StpUtil.checkRole("admin");
        List<User> users;
        if (role != null) {
            users = userService.getUsersByRole(role);
        } else if (status != null) {
            users = userService.getUsersByStatus(status);
        } else {
            users = userService.getAllUsers();
        }
        return Result.success(users);
    }

    /**
     * 获取用户详情（管理员）
     */
    @GetMapping("/admin/{id}")
    public Result<User> getById(@PathVariable Integer id) {
        StpUtil.checkRole("admin");
        User user = userService.getUserById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    /**
     * 获取用户统计（管理员）
     */
    @GetMapping("/admin/stats")
    public Result<Map<String, Object>> getStats() {
        StpUtil.checkRole("admin");
        return Result.success(userService.getStats());
    }

    /**
     * 创建用户（管理员）
     */
    @PostMapping("/admin")
    public Result<User> create(@RequestBody User user) {
        StpUtil.checkRole("admin");
        return Result.success(userService.createUser(user));
    }

    /**
     * 更新用户（管理员）
     */
    @PutMapping("/admin/{id}")
    public Result<Void> update(@PathVariable Integer id, @RequestBody User user) {
        StpUtil.checkRole("admin");
        user.setId(id);
        userService.updateUserByAdmin(user);
        return Result.success();
    }

    /**
     * 启用用户（管理员）
     */
    @PutMapping("/admin/{id}/enable")
    public Result<Void> enable(@PathVariable Integer id) {
        StpUtil.checkRole("admin");
        userService.updateUserStatus(id, 1);
        return Result.success();
    }

    /**
     * 禁用用户（管理员）
     */
    @PutMapping("/admin/{id}/disable")
    public Result<Void> disable(@PathVariable Integer id) {
        StpUtil.checkRole("admin");
        // 不能禁用自己
        if (id.equals(StpUtil.getLoginIdAsInt())) {
            return Result.error("不能禁用自己的账号");
        }
        userService.updateUserStatus(id, 0);
        return Result.success();
    }

    /**
     * 删除用户（管理员）
     */
    @DeleteMapping("/admin/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        StpUtil.checkRole("admin");
        // 不能删除自己
        if (id.equals(StpUtil.getLoginIdAsInt())) {
            return Result.error("不能删除自己的账号");
        }
        userService.deleteUser(id);
        return Result.success();
    }
}
