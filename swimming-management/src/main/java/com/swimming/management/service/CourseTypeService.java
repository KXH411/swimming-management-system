package com.swimming.management.service;

import com.swimming.management.entity.CourseType;
import com.swimming.management.repository.CourseTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseTypeService {

    @Autowired
    private CourseTypeRepository courseTypeRepository;

    public List<CourseType> findAll() {
        return courseTypeRepository.findAll();
    }

    public CourseType findById(Integer id) {
        return courseTypeRepository.findById(id).orElse(null);
    }

    public CourseType save(CourseType courseType) {
        if (courseType.getAddtime() == null) {
            courseType.setAddtime(LocalDateTime.now());
        }
        return courseTypeRepository.save(courseType);
    }

    public void delete(Integer id) {
        courseTypeRepository.deleteById(id);
    }
}
