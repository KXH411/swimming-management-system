package com.swimming.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "xiangmumingcheng", nullable = false, length = 200)
    private String xiangmumingcheng;

    @Column(name = "leixing", nullable = false, length = 200)
    private String leixing;

    @Column(name = "jiaolianmingcheng", nullable = false, length = 200)
    private String jiaolianmingcheng;

    @Column(name = "yonghuzhanghao", nullable = false, length = 200)
    private String yonghuzhanghao;

    @Column(name = "yonghuxingming", nullable = false, length = 200)
    private String yonghuxingming;

    @Column(name = "lianxidianhua", nullable = false, length = 20)
    private String lianxidianhua;

    @Column(name = "crossuserid", nullable = false)
    private Integer crossuserid;

    @Column(name = "yuyueshijian")
    private LocalDateTime yuyueshijian;

    @Column(name = "zhuangtai", length = 50)
    private String zhuangtai = "待确认";

    @Column(name = "beizhu", columnDefinition = "TEXT")
    private String beizhu;

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

    public String getLeixing() {
        return leixing;
    }

    public void setLeixing(String leixing) {
        this.leixing = leixing;
    }

    public String getJiaolianmingcheng() {
        return jiaolianmingcheng;
    }

    public void setJiaolianmingcheng(String jiaolianmingcheng) {
        this.jiaolianmingcheng = jiaolianmingcheng;
    }

    public String getYonghuzhanghao() {
        return yonghuzhanghao;
    }

    public void setYonghuzhanghao(String yonghuzhanghao) {
        this.yonghuzhanghao = yonghuzhanghao;
    }

    public String getYonghuxingming() {
        return yonghuxingming;
    }

    public void setYonghuxingming(String yonghuxingming) {
        this.yonghuxingming = yonghuxingming;
    }

    public String getLianxidianhua() {
        return lianxidianhua;
    }

    public void setLianxidianhua(String lianxidianhua) {
        this.lianxidianhua = lianxidianhua;
    }

    public Integer getCrossuserid() {
        return crossuserid;
    }

    public void setCrossuserid(Integer crossuserid) {
        this.crossuserid = crossuserid;
    }

    public LocalDateTime getYuyueshijian() {
        return yuyueshijian;
    }

    public void setYuyueshijian(LocalDateTime yuyueshijian) {
        this.yuyueshijian = yuyueshijian;
    }

    public String getZhuangtai() {
        return zhuangtai;
    }

    public void setZhuangtai(String zhuangtai) {
        this.zhuangtai = zhuangtai;
    }

    public String getBeizhu() {
        return beizhu;
    }

    public void setBeizhu(String beizhu) {
        this.beizhu = beizhu;
    }

    public LocalDateTime getAddtime() {
        return addtime;
    }

    public void setAddtime(LocalDateTime addtime) {
        this.addtime = addtime;
    }

    public Reservation() {}
}