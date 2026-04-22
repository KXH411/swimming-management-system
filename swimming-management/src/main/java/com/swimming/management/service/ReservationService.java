package com.swimming.management.service;

import com.swimming.management.entity.Reservation;
import com.swimming.management.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    // 🔥 获取用户的所有预约记录 - 使用crossuserid
    public List<Reservation> getUserReservations(Integer userId) {
        return reservationRepository.findByCrossuserid(userId);
    }

    // 🔥 获取所有预约记录（管理员用）
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    // 🔥 根据状态获取用户的预约记录
    public List<Reservation> getUserReservationsByStatus(Integer userId, String zhuangtai) {
        return reservationRepository.findByCrossuseridAndZhuangtai(userId, zhuangtai);
    }

    // 🔥 创建预约
    public Reservation createReservation(Reservation reservation) {
        reservation.setAddtime(LocalDateTime.now());
        if (reservation.getZhuangtai() == null) {
            reservation.setZhuangtai("待确认"); // 默认状态
        }
        return reservationRepository.save(reservation);
    }

    // 🔥 取消预约
    public Reservation cancelReservation(Integer reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("预约记录不存在"));
        reservation.setZhuangtai("已取消");
        reservationRepository.deleteById(reservationId);
        return reservationRepository.save(reservation);
    }

    // 🔥 确认预约
    public Reservation confirmReservation(Integer reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("预约记录不存在"));
        reservation.setZhuangtai("已确认");
        return reservationRepository.save(reservation);
    }

    // 🔥 删除预约
    public void deleteReservation(Integer reservationId) {
        reservationRepository.deleteById(reservationId);
    }

    // 🔥 获取预约详情
    public Reservation getReservationById(Integer reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("预约记录不存在"));
    }

    // 🔥 搜索预约记录
    public List<Reservation> searchReservations(String keyword) {
        return reservationRepository.findByXiangmumingchengContaining(keyword);
    }
}