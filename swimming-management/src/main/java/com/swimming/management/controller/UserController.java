package com.swimming.management.controller;

import com.swimming.management.entity.User;
import com.swimming.management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
//@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            User registeredUser = userService.register(user);
            result.put("success", true);
            result.put("message", "注册成功");
            result.put("data", registeredUser);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            User loggedInUser = userService.login(user.getYonghuzhanghao(), user.getMima());
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("data", loggedInUser);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    // 🔥 使用Integer类型
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = userService.findById(id);
            if (user != null) {
                result.put("success", true);
                result.put("data", user);
                return ResponseEntity.ok(result);
            } else {
                result.put("success", false);
                result.put("message", "用户不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取用户信息失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 🔥 使用Integer类型
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Integer id, @RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 设置用户ID
            user.setId(id);
            User updatedUser = userService.updateUser(user);
            result.put("success", true);
            result.put("message", "用户信息更新成功");
            result.put("data", updatedUser);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新用户信息失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 🔥 更新用户信息接口（简单方式）
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            User updatedUser = userService.updateUser(user);
            result.put("success", true);
            result.put("message", "用户信息更新成功");
            result.put("data", updatedUser);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新用户信息失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 🔥 更新个人资料接口
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            User updatedUser = userService.updateUser(user);
            result.put("success", true);
            result.put("message", "个人信息更新成功");
            result.put("data", updatedUser);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新个人信息失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 删除用户接口
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            System.out.println("🔄 后端收到删除请求，用户ID: " + id);

            userService.deleteUser(id);

            System.out.println("✅ 用户删除成功，ID: " + id);
            result.put("success", true);
            result.put("message", "用户删除成功");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.out.println("❌ 删除用户失败，ID: " + id + ", 错误: " + e.getMessage());
            result.put("success", false);
            result.put("message", "删除用户失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 重置密码接口
    @PostMapping("/reset-password/{id}")
    public ResponseEntity<Map<String, Object>> resetPassword(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            userService.resetPassword(id);
            result.put("success", true);
            result.put("message", "密码重置成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "重置密码失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 批量删除用户接口
    @PostMapping("/batch-delete")
    public ResponseEntity<Map<String, Object>> batchDeleteUsers(@RequestBody List<Integer> ids) {
        Map<String, Object> result = new HashMap<>();
        try {
            userService.batchDeleteUsers(ids);
            result.put("success", true);
            result.put("message", "批量删除成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "批量删除失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}