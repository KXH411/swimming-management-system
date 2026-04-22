package com.swimming.management.repository;

import com.swimming.management.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    // 🔥 根据用户ID查找预约记录 - 使用crossuserid字段
    List<Reservation> findByCrossuserid(Integer crossuserid);

    // 🔥 根据用户账号查找预约记录
    List<Reservation> findByYonghuzhanghao(String yonghuzhanghao);

    // 🔥 根据状态查找预约记录
    List<Reservation> findByCrossuseridAndZhuangtai(Integer crossuserid, String zhuangtai);

    // 🔥 根据项目名称搜索预约记录
    List<Reservation> findByXiangmumingchengContaining(String keyword);
}