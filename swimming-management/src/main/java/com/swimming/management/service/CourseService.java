package com.swimming.management.service;

import com.swimming.management.entity.Course;
import com.swimming.management.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    // 获取所有课程
    public List<Course> getAllCourses() {
        return courseRepository.findAllByOrderByAddtimeDesc();
    }

    // 根据ID获取课程
    public Course getCourseById(Integer id) {
        return courseRepository.findById(id).orElse(null);
    }

    // 根据类型获取课程
    public List<Course> getCoursesByType(String leixing) {
        return courseRepository.findByLeixingOrderByAddtimeDesc(leixing);
    }

    // 搜索课程
    public List<Course> searchCourses(String keyword) {
        return courseRepository.findByXiangmumingchengContainingOrKechengjianjieContainingOrderByAddtimeDesc(
                keyword, keyword);
    }

    // 获取热门课程（按收藏数排序）
    public List<Course> getPopularCourses() {
        return courseRepository.findTop6ByOrderByStoreupnumDesc();
    }

    // 获取最新课程
    public List<Course> getLatestCourses() {
        return courseRepository.findTop6ByOrderByAddtimeDesc();
    }

    // 创建课程
    public Course createCourse(Course course) {
        // 设置创建时间
        if (course.getAddtime() == null) {
            course.setAddtime(LocalDateTime.now());
        }

        // 设置默认收藏数
        if (course.getStoreupnum() == null) {
            course.setStoreupnum(0);
        }

        return courseRepository.save(course);
    }

    // 更新课程
    public Course updateCourse(Course course) {
        if (course.getId() == null) {
            throw new RuntimeException("课程ID不能为空");
        }

        // 检查课程是否存在
        Course existingCourse = getCourseById(course.getId());
        if (existingCourse == null) {
            throw new RuntimeException("课程不存在，ID: " + course.getId());
        }

        // 保留原有的创建时间和不想更新的字段
        course.setAddtime(existingCourse.getAddtime());

        return courseRepository.save(course);
    }

    // 删除课程
    public void deleteCourse(Integer id) {
        // 检查课程是否存在
        if (!courseRepository.existsById(id)) {
            throw new RuntimeException("课程不存在，ID: " + id);
        }
        courseRepository.deleteById(id);
    }

    // 增加收藏数
    public Course incrementStoreupnum(Integer id) {
        Course course = getCourseById(id);
        if (course == null) {
            throw new RuntimeException("课程不存在，ID: " + id);
        }
        course.setStoreupnum(course.getStoreupnum() + 1);
        return courseRepository.save(course);
    }

    // 减少收藏数
    public Course decrementStoreupnum(Integer id) {
        Course course = getCourseById(id);
        if (course == null) {
            throw new RuntimeException("课程不存在，ID: " + id);
        }
        if (course.getStoreupnum() > 0) {
            course.setStoreupnum(course.getStoreupnum() - 1);
        }
        return courseRepository.save(course);
    }

    // 获取所有课程类型
    public List<String> getAllCourseTypes() {
        return courseRepository.findDistinctLeixing();
    }
}