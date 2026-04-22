package com.swimming.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "course_type")
public class CourseType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "kechengleixing", nullable = false, length = 200)
    private String kechengleixing;

    @Column(name = "addtime")
    private LocalDateTime addtime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKechengleixing() {
        return kechengleixing;
    }

    public void setKechengleixing(String kechengleixing) {
        this.kechengleixing = kechengleixing;
    }

    public LocalDateTime getAddtime() {
        return addtime;
    }

    public void setAddtime(LocalDateTime addtime) {
        this.addtime = addtime;
    }

    public CourseType() {}
}