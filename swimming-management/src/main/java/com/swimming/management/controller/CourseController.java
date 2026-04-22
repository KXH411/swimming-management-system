package com.swimming.management.controller;

import com.swimming.management.entity.Course;
import com.swimming.management.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    // 获取所有课程列表
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllCourses() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Course> courses = courseService.getAllCourses();
            result.put("success", true);
            result.put("data", courses);
            result.put("message", "获取课程列表成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取课程列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 根据ID获取课程详情
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCourseById(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Course course = courseService.getCourseById(id);
            if (course != null) {
                result.put("success", true);
                result.put("data", course);
                return ResponseEntity.ok(result);
            } else {
                result.put("success", false);
                result.put("message", "课程不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取课程详情失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 根据类型获取课程
    @GetMapping("/type/{leixing}")
    public ResponseEntity<Map<String, Object>> getCoursesByType(@PathVariable String leixing) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Course> courses = courseService.getCoursesByType(leixing);
            result.put("success", true);
            result.put("data", courses);
            result.put("message", "获取课程列表成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取课程列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 搜索课程
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCourses(@RequestParam String keyword) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Course> courses = courseService.searchCourses(keyword);
            result.put("success", true);
            result.put("data", courses);
            result.put("message", "搜索课程成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "搜索课程失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 获取热门课程
    @GetMapping("/popular")
    public ResponseEntity<Map<String, Object>> getPopularCourses() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Course> courses = courseService.getPopularCourses();
            result.put("success", true);
            result.put("data", courses);
            result.put("message", "获取热门课程成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取热门课程失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 获取最新课程
    @GetMapping("/latest")
    public ResponseEntity<Map<String, Object>> getLatestCourses() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Course> courses = courseService.getLatestCourses();
            result.put("success", true);
            result.put("data", courses);
            result.put("message", "获取最新课程成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取最新课程失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 创建课程
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createCourse(@RequestBody Course course) {
        Map<String, Object> result = new HashMap<>();
        try {
            Course createdCourse = courseService.createCourse(course);
            result.put("success", true);
            result.put("data", createdCourse);
            result.put("message", "课程创建成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "课程创建失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 更新课程
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCourse(@PathVariable Integer id, @RequestBody Course course) {
        Map<String, Object> result = new HashMap<>();
        try {
            course.setId(id);
            Course updatedCourse = courseService.updateCourse(course);
            result.put("success", true);
            result.put("data", updatedCourse);
            result.put("message", "课程更新成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "课程更新失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 删除课程
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCourse(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            courseService.deleteCourse(id);
            result.put("success", true);
            result.put("message", "课程删除成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "课程删除失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 增加收藏数
    @PutMapping("/{id}/favorite")
    public ResponseEntity<Map<String, Object>> incrementFavorite(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Course course = courseService.incrementStoreupnum(id);
            result.put("success", true);
            result.put("data", course);
            result.put("message", "收藏成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "收藏失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 减少收藏数
    @PutMapping("/{id}/unfavorite")
    public ResponseEntity<Map<String, Object>> decrementFavorite(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Course course = courseService.decrementStoreupnum(id);
            result.put("success", true);
            result.put("data", course);
            result.put("message", "取消收藏成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "取消收藏失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}