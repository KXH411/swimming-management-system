package com.swimming.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "xiangmumingcheng", nullable = false, length = 200)
    private String xiangmumingcheng;

    @Column(name = "fengmian", length = 500)
    private String fengmian;

    @Column(name = "leixing", nullable = false, length = 200)
    private String leixing;

    @Column(name = "xuexishipin", length = 500)
    private String xuexishipin;

    @Column(name = "kechengjianjie", columnDefinition = "TEXT")
    private String kechengjianjie;

    @Column(name = "kechengxiangqing", columnDefinition = "TEXT")
    private String kechengxiangqing;

    @Column(name = "shichang")
    private Integer shichang;

    @Column(name = "jiage", precision = 10, scale = 2)
    private BigDecimal jiage;

    @Column(name = "fabushijian")
    private LocalDateTime fabushijian;

    @Column(name = "jiaolianmingcheng", length = 200)
    private String jiaolianmingcheng;

    @Column(name = "jiaolianid")
    private Integer jiaolianid;

    @Column(name = "storeupnum")
    private Integer storeupnum = 0;

    @Column(name = "addtime")
    private LocalDateTime addtime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getXiangmumingcheng() {
        return xiangmumingcheng;
    }

    public void setXiangmumingcheng(String xiangmumingcheng) {
        this.xiangmumingcheng = xiangmumingcheng;
    }

    public String getFengmian() {
        return fengmian;
    }

    public void setFengmian(String fengmian) {
        this.fengmian = fengmian;
    }

    public String getLeixing() {
        return leixing;
    }

    public void setLeixing(String leixing) {
        this.leixing = leixing;
    }

    public String getXuexishipin() {
        return xuexishipin;
    }

    public void setXuexishipin(String xuexishipin) {
        this.xuexishipin = xuexishipin;
    }

    public String getKechengjianjie() {
        return kechengjianjie;
    }

    public void setKechengjianjie(String kechengjianjie) {
        this.kechengjianjie = kechengjianjie;
    }

    public String getKechengxiangqing() {
        return kechengxiangqing;
    }

    public void setKechengxiangqing(String kechengxiangqing) {
        this.kechengxiangqing = kechengxiangqing;
    }

    public Integer getShichang() {
        return shichang;
    }

    public void setShichang(Integer shichang) {
        this.shichang = shichang;
    }

    public BigDecimal getJiage() {
        return jiage;
    }

    public void setJiage(BigDecimal jiage) {
        this.jiage = jiage;
    }

    public LocalDateTime getFabushijian() {
        return fabushijian;
    }

    public void setFabushijian(LocalDateTime fabushijian) {
        this.fabushijian = fabushijian;
    }

    public String getJiaolianmingcheng() {
        return jiaolianmingcheng;
    }

    public void setJiaolianmingcheng(String jiaolianmingcheng) {
        this.jiaolianmingcheng = jiaolianmingcheng;
    }

    public Integer getJiaolianid() {
        return jiaolianid;
    }

    public void setJiaolianid(Integer jiaolianid) {
        this.jiaolianid = jiaolianid;
    }

    public Integer getStoreupnum() {
        return storeupnum;
    }

    public void setStoreupnum(Integer storeupnum) {
        this.storeupnum = storeupnum;
    }

    public LocalDateTime getAddtime() {
        return addtime;
    }

    public void setAddtime(LocalDateTime addtime) {
        this.addtime = addtime;
    }

    public Course() {}
}