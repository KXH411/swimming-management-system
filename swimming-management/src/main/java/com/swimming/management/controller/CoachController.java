package com.swimming.management.controller;

import com.swimming.management.entity.Coach;
import com.swimming.management.service.CoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coach")
@CrossOrigin(origins = "*")
public class CoachController {

    @Autowired
    private CoachService coachService;

    @GetMapping("/list")
    public ResponseEntity<List<Coach>> getAllCoaches() {
        return ResponseEntity.ok(coachService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coach> getCoachById(@PathVariable Integer id) {
        Coach coach = coachService.findById(id);
        return coach != null ? ResponseEntity.ok(coach) : ResponseEntity.notFound().build();
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addCoach(@RequestBody Coach coach) {
        Map<String, Object> result = new HashMap<>();
        try {
            Coach savedCoach = coachService.save(coach);
            result.put("success", true);
            result.put("message", "教练添加成功");
            result.put("data", savedCoach);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 更新教练信息
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateCoach(@RequestBody Coach coach) {
        Map<String, Object> result = new HashMap<>();
        try {
            Coach updatedCoach = coachService.updateCoach(coach);
            result.put("success", true);
            result.put("message", "教练信息更新成功");
            result.put("data", updatedCoach);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新教练信息失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 删除教练
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteCoach(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            coachService.deleteCoach(id);
            result.put("success", true);
            result.put("message", "教练删除成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除教练失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 搜索教练
    @GetMapping("/search")
    public ResponseEntity<List<Coach>> searchCoaches(@RequestParam String keyword) {
        return ResponseEntity.ok(coachService.searchCoaches(keyword));
    }

    @GetMapping("/gender/{xingbie}")
    public ResponseEntity<List<Coach>> getCoachesByGender(@PathVariable String xingbie) {
        return ResponseEntity.ok(coachService.findByGender(xingbie));
    }

    @GetMapping("/specialty/{zhuanye}")
    public ResponseEntity<List<Coach>> getCoachesBySpecialty(@PathVariable String zhuanye) {
        return ResponseEntity.ok(coachService.findBySpecialty(zhuanye));
    }
}