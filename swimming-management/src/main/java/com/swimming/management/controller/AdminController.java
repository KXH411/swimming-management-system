package com.swimming.management.controller;

import com.swimming.management.entity.Admin;
import com.swimming.management.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Admin admin) {
        Map<String, Object> result = new HashMap<>();
        try {
            Admin loggedInAdmin = adminService.login(admin.getUsername(), admin.getPassword());
            result.put("success", true);
            result.put("message", "管理员登录成功");
            result.put("data", loggedInAdmin);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}