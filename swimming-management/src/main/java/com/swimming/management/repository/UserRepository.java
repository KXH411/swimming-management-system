package com.swimming.management.repository;

import com.swimming.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // 根据账号查找用户 - 返回 Optional
    Optional<User> findByYonghuzhanghao(String yonghuzhanghao);

    // 检查账号是否存在
    boolean existsByYonghuzhanghao(String yonghuzhanghao);

    // JpaRepository 已经提供了以下方法：
    // - findAll()
    // - findById(Integer id)
    // - save(User user)
    // - deleteById(Integer id)
    // - deleteAllById(Iterable<Integer> ids)
    // - existsById(Integer id)
}