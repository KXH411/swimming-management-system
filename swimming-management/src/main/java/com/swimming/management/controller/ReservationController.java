package com.swimming.management.controller;

import com.swimming.management.entity.Reservation;
import com.swimming.management.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    // 🔥 获取用户的预约记录 - 修复接口路径
    @GetMapping("/user/{userId}/reservations")
    public ResponseEntity<Map<String, Object>> getUserReservations(@PathVariable Integer userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Reservation> reservations = reservationService.getUserReservations(userId);
            result.put("success", true);
            result.put("data", reservations);
            result.put("message", "获取预约记录成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取预约记录失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 🔥 创建预约
    @PostMapping("/reservations")
    public ResponseEntity<Map<String, Object>> createReservation(@RequestBody Reservation reservation) {
        Map<String, Object> result = new HashMap<>();
        try {
            Reservation createdReservation = reservationService.createReservation(reservation);
            result.put("success", true);
            result.put("data", createdReservation);
            result.put("message", "预约创建成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "预约创建失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 🔥 取消预约
    @PutMapping("/reservations/{reservationId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelReservation(@PathVariable Integer reservationId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Reservation cancelledReservation = reservationService.cancelReservation(reservationId);
            result.put("success", true);
            result.put("data", cancelledReservation);
            result.put("message", "预约取消成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "预约取消失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 🔥 确认预约
    @PutMapping("/reservations/{reservationId}/confirm")
    public ResponseEntity<Map<String, Object>> confirmReservation(@PathVariable Integer reservationId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Reservation confirmedReservation = reservationService.confirmReservation(reservationId);
            result.put("success", true);
            result.put("data", confirmedReservation);
            result.put("message", "预约确认成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "预约确认失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 🔥 删除预约
    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<Map<String, Object>> deleteReservation(@PathVariable Integer reservationId) {
        Map<String, Object> result = new HashMap<>();
        try {
            reservationService.deleteReservation(reservationId);
            result.put("success", true);
            result.put("message", "预约删除成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "预约删除失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 获取所有预约记录（管理员用）
    @GetMapping("/reservations/list")
    public ResponseEntity<Map<String, Object>> getAllReservations() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Reservation> reservations = reservationService.getAllReservations();
            result.put("success", true);
            result.put("data", reservations);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 🔥 获取预约详情
    @GetMapping("/reservations/{reservationId}")
    public ResponseEntity<Map<String, Object>> getReservationById(@PathVariable Integer reservationId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Reservation reservation = reservationService.getReservationById(reservationId);
            result.put("success", true);
            result.put("data", reservation);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取预约详情失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}