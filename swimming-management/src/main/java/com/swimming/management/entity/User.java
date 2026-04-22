package com.swimming.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "yonghuzhanghao", nullable = false, length = 200)
    private String yonghuzhanghao;

    @Column(name = "mima", nullable = false, length = 200)
    private String mima;

    @Column(name = "yonghuxingming", nullable = false, length = 200)
    private String yonghuxingming;

    @Column(name = "xingbie", length = 10)
    private String xingbie;

    @Column(name = "touxiang", length = 500)
    private String touxiang;

    @Column(name = "lianxidianhua", length = 20)
    private String lianxidianhua;

    @Column(name = "addtime")
    private LocalDateTime addtime;
    public String getYonghuzhanghao() {
        return this.yonghuzhanghao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getYonghuxingming() {
        return yonghuxingming;
    }

    public void setYonghuxingming(String yonghuxingming) {
        this.yonghuxingming = yonghuxingming;
    }

    public String getXingbie() {
        return xingbie;
    }

    public void setXingbie(String xingbie) {
        this.xingbie = xingbie;
    }

    public String getTouxiang() {
        return touxiang;
    }

    public void setTouxiang(String touxiang) {
        this.touxiang = touxiang;
    }

    public String getLianxidianhua() {
        return lianxidianhua;
    }

    public void setLianxidianhua(String lianxidianhua) {
        this.lianxidianhua = lianxidianhua;
    }

    public LocalDateTime getAddtime() {
        return addtime;
    }

    public void setAddtime(LocalDateTime addtime) {
        this.addtime = addtime;
    }

    public void setYonghuzhanghao(String yonghuzhanghao) {
        this.yonghuzhanghao = yonghuzhanghao;
    }

    public String getMima() {
        return this.mima;
    }

    public void setMima(String mima) {
        this.mima = mima;
    }
}