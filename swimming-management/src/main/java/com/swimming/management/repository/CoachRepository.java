package com.swimming.management.repository;

import com.swimming.management.entity.Coach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CoachRepository extends JpaRepository<Coach, Integer> {

    // 根据性别查找教练
    List<Coach> findByXingbie(String xingbie);

    // 根据专业领域查找教练
    List<Coach> findByZhuanye(String zhuanye);

    // 搜索教练（姓名、简介、专业）
    List<Coach> findByJiaolianxingmingContainingOrJianjieContainingOrZhuanyeContaining(
            String name, String description, String specialty);

    // 检查教练是否存在
    boolean existsById(Integer id);
}