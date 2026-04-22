package com.swimming.management.service;

import com.swimming.management.entity.Favorite;
import com.swimming.management.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    public List<Favorite> findAll() {
        return favoriteRepository.findAll();
    }

    public Favorite findById(Integer id) {
        return favoriteRepository.findById(id).orElse(null);
    }

    public Favorite save(Favorite favorite) {
        if (favorite.getAddtime() == null) {
            favorite.setAddtime(LocalDateTime.now());
        }
        return favoriteRepository.save(favorite);
    }

    public void delete(Integer id) {
        favoriteRepository.deleteById(id);
    }

    public List<Favorite> findByUserId(Integer userId) {
        return favoriteRepository.findByUserid(userId);
    }

    public boolean isFavorited(Integer userId, String tableName, Integer refId) {
        return favoriteRepository.existsByUseridAndTablenameAndRefid(userId, tableName, refId);
    }

    public Favorite toggleFavorite(Integer userId, String tableName, String name, String picture, String type, Integer refId) {
        boolean exists = isFavorited(userId, tableName, refId);

        if (exists) {
            // 取消收藏
            Favorite favorite = favoriteRepository.findByUseridAndTablename(userId, tableName)
                    .stream()
                    .filter(f -> f.getRefid().equals(refId))
                    .findFirst()
                    .orElse(null);
            if (favorite != null) {
                favoriteRepository.delete(favorite);
                return null;
            }
        } else {
            // 添加收藏
            Favorite favorite = new Favorite();
            favorite.setUserid(userId);
            favorite.setTablename(tableName);
            favorite.setName(name);
            favorite.setPicture(picture);
            favorite.setType(type);
            favorite.setRefid(refId);
            return save(favorite);
        }
        return null;
    }
}