package com.swimming.management.controller;

import com.swimming.management.entity.CourseType;
import com.swimming.management.service.CourseTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/course-type")
@CrossOrigin(origins = "*")
public class CourseTypeController {

    @Autowired
    private CourseTypeService courseTypeService;

    @GetMapping("/list")
    public ResponseEntity<List<CourseType>> getAllCourseTypes() {
        return ResponseEntity.ok(courseTypeService.findAll());
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addCourseType(@RequestBody CourseType courseType) {
        Map<String, Object> result = new HashMap<>();
        try {
            CourseType savedCourseType = courseTypeService.save(courseType);
            result.put("success", true);
            result.put("message", "课程分类添加成功");
            result.put("data", savedCourseType);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCourseType(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            courseTypeService.delete(id);
            result.put("success", true);
            result.put("message", "课程分类删除成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}