package com.swimming.management.service;

import com.swimming.management.entity.User;
import com.swimming.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User register(User user) {
        // 检查用户名是否已存在
        Optional<User> existingUser = userRepository.findByYonghuzhanghao(user.getYonghuzhanghao());
        if (existingUser.isPresent()) {
            throw new RuntimeException("用户名已存在，请选择其他用户名");
        }

        // 设置创建时间
        user.setAddtime(LocalDateTime.now());

        return userRepository.save(user);
    }

    public User login(String yonghuzhanghao, String mima) {
        Optional<User> userOpt = userRepository.findByYonghuzhanghao(yonghuzhanghao);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getMima().equals(mima)) {
                return user;
            } else {
                throw new RuntimeException("密码错误");
            }
        }
        throw new RuntimeException("用户名不存在");
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    // 🔥 确保有这个updateUser方法
    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }

        // 检查用户是否存在
        User existingUser = findById(user.getId());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在，ID: " + user.getId());
        }

        // 保留原有的创建时间和其他不想更新的字段
        user.setAddtime(existingUser.getAddtime());

        return userRepository.save(user);
    }

    // 保持原有的update方法作为别名
    public User update(User user) {
        return updateUser(user);
    }

    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

    // 添加检查用户名是否存在的方法
    public boolean checkUsernameExists(String yonghuzhanghao) {
        return userRepository.findByYonghuzhanghao(yonghuzhanghao).isPresent();
    }

    // 🔥 🔥 🔥 新增的删除相关方法 - 添加到Controller调用的方法

    /**
     * 删除用户 - 供Controller调用
     */
    public void deleteUser(Integer id) {
        System.out.println("🔄 UserService.deleteUser 被调用，ID: " + id);

        // 检查用户是否存在
        if (!userRepository.existsById(id)) {
            System.out.println("❌ 用户不存在，ID: " + id);
            throw new RuntimeException("用户不存在，ID: " + id);
        }

        // 执行删除
        userRepository.deleteById(id);
        System.out.println("✅ 用户删除完成，ID: " + id);
    }

    /**
     * 重置用户密码 - 供Controller调用
     */
    public void resetPassword(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在，ID: " + id));

        // 重置密码为默认密码 "123456"
        user.setMima("123456");
        userRepository.save(user);
    }

    /**
     * 批量删除用户 - 供Controller调用
     */
    public void batchDeleteUsers(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("用户ID列表不能为空");
        }

        // 检查所有用户是否存在
        for (Integer id : ids) {
            if (!userRepository.existsById(id)) {
                throw new RuntimeException("用户不存在，ID: " + id);
            }
        }

        // 批量删除
        userRepository.deleteAllById(ids);
    }

    /**
     * 根据用户名查找用户
     */
    public User findByUsername(String yonghuzhanghao) {
        return userRepository.findByYonghuzhanghao(yonghuzhanghao).orElse(null);
    }

    /**
     * 检查用户是否存在
     */
    public boolean existsById(Integer id) {
        return userRepository.existsById(id);
    }
}