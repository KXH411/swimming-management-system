package com.swimming.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "coach")
public class Coach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "jiaolianxingming", nullable = false, length = 200)
    private String jiaolianxingming;

    @Column(name = "zhaopian", length = 500)
    private String zhaopian;

    @Column(name = "xingbie", length = 10)
    private String xingbie;

    @Column(name = "lianxidianhua", length = 20)
    private String lianxidianhua;

    @Column(name = "jianjie", columnDefinition = "TEXT")
    private String jianjie;

    @Column(name = "zhuanye", length = 200)
    private String zhuanye;

    @Column(name = "addtime")
    private LocalDateTime addtime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJiaolianxingming() {
        return jiaolianxingming;
    }

    public void setJiaolianxingming(String jiaolianxingming) {
        this.jiaolianxingming = jiaolianxingming;
    }

    public String getZhaopian() {
        return zhaopian;
    }

    public void setZhaopian(String zhaopian) {
        this.zhaopian = zhaopian;
    }

    public String getXingbie() {
        return xingbie;
    }

    public void setXingbie(String xingbie) {
        this.xingbie = xingbie;
    }

    public String getLianxidianhua() {
        return lianxidianhua;
    }

    public void setLianxidianhua(String lianxidianhua) {
        this.lianxidianhua = lianxidianhua;
    }

    public String getJianjie() {
        return jianjie;
    }

    public void setJianjie(String jianjie) {
        this.jianjie = jianjie;
    }

    public String getZhuanye() {
        return zhuanye;
    }

    public void setZhuanye(String zhuanye) {
        this.zhuanye = zhuanye;
    }

    public LocalDateTime getAddtime() {
        return addtime;
    }

    public void setAddtime(LocalDateTime addtime) {
        this.addtime = addtime;
    }

    public Coach() {}
}