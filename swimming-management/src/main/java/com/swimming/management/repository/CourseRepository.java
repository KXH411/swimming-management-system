package com.swimming.management.repository;

import com.swimming.management.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    // 按添加时间倒序获取所有课程
    List<Course> findAllByOrderByAddtimeDesc();

    // 根据类型获取课程，按添加时间倒序
    List<Course> findByLeixingOrderByAddtimeDesc(String leixing);

    // 搜索课程（项目名称或课程简介）
    List<Course> findByXiangmumingchengContainingOrKechengjianjieContainingOrderByAddtimeDesc(
            String xiangmumingcheng, String kechengjianjie);

    // 获取热门课程（按收藏数排序，前6个）
    List<Course> findTop6ByOrderByStoreupnumDesc();

    // 获取最新课程（按添加时间排序，前6个）
    List<Course> findTop6ByOrderByAddtimeDesc();

    // 获取所有不同的课程类型
    @Query("SELECT DISTINCT c.leixing FROM Course c ORDER BY c.leixing")
    List<String> findDistinctLeixing();

    // 检查课程是否存在
    boolean existsById(Integer id);
}