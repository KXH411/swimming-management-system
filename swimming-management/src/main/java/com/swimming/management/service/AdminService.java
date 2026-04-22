package com.swimming.management.service;

import com.swimming.management.entity.Admin;
import com.swimming.management.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public Admin login(String username, String password) {
        Optional<Admin> adminOpt = adminRepository.findByUsername(username);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (admin.getPassword().equals(password)) {
                return admin;
            } else {
                throw new RuntimeException("管理员密码错误");
            }
        }
        throw new RuntimeException("管理员账号不存在");
    }
}