package com.swimming.management.controller;

import com.swimming.management.entity.Favorite;
import com.swimming.management.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorite")
@CrossOrigin(origins = "*")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Favorite>> getUserFavorites(@PathVariable Integer userId) {
        return ResponseEntity.ok(favoriteService.findByUserId(userId));
    }

    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleFavorite(@RequestBody Map<String, Object> favoriteData) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer userId = (Integer) favoriteData.get("userId");
            String tableName = (String) favoriteData.get("tableName");
            String name = (String) favoriteData.get("name");
            String picture = (String) favoriteData.get("picture");
            String type = (String) favoriteData.get("type");
            Integer refId = (Integer) favoriteData.get("refId");

            Favorite favorite = favoriteService.toggleFavorite(userId, tableName, name, picture, type, refId);

            if (favorite == null) {
                result.put("success", true);
                result.put("message", "取消收藏成功");
                result.put("isFavorited", false);
            } else {
                result.put("success", true);
                result.put("message", "收藏成功");
                result.put("isFavorited", true);
                result.put("data", favorite);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkFavorite(
            @RequestParam Integer userId,
            @RequestParam String tableName,
            @RequestParam Integer refId) {
        Map<String, Object> result = new HashMap<>();
        boolean isFavorited = favoriteService.isFavorited(userId, tableName, refId);
        result.put("isFavorited", isFavorited);
        return ResponseEntity.ok(result);
    }
}