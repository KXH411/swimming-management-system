package com.swimming.management.service;

import com.swimming.management.entity.Coach;
import com.swimming.management.repository.CoachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CoachService {

    @Autowired
    private CoachRepository coachRepository;

    public List<Coach> findAll() {
        return coachRepository.findAll();
    }

    public Coach findById(Integer id) {
        return coachRepository.findById(id).orElse(null);
    }

    public Coach save(Coach coach) {
        // 设置创建时间
        if (coach.getAddtime() == null) {
            coach.setAddtime(LocalDateTime.now());
        }
        return coachRepository.save(coach);
    }

    // 🔥 新增：更新教练信息
    public Coach updateCoach(Coach coach) {
        if (coach.getId() == null) {
            throw new RuntimeException("教练ID不能为空");
        }

        // 检查教练是否存在
        Coach existingCoach = findById(coach.getId());
        if (existingCoach == null) {
            throw new RuntimeException("教练不存在，ID: " + coach.getId());
        }

        // 保留原有的创建时间
        coach.setAddtime(existingCoach.getAddtime());

        return coachRepository.save(coach);
    }

    // 🔥 新增：删除教练
    public void deleteCoach(Integer id) {
        // 检查教练是否存在
        if (!coachRepository.existsById(id)) {
            throw new RuntimeException("教练不存在，ID: " + id);
        }
        coachRepository.deleteById(id);
    }

    // 🔥 新增：搜索教练
    public List<Coach> searchCoaches(String keyword) {
        return coachRepository.findByJiaolianxingmingContainingOrJianjieContainingOrZhuanyeContaining(
                keyword, keyword, keyword);
    }

    public List<Coach> findByGender(String xingbie) {
        return coachRepository.findByXingbie(xingbie);
    }

    public List<Coach> findBySpecialty(String zhuanye) {
        return coachRepository.findByZhuanye(zhuanye);
    }
}