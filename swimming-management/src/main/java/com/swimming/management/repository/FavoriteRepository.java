package com.swimming.management.repository;

import com.swimming.management.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    List<Favorite> findByUserid(Integer userid);
    List<Favorite> findByUseridAndTablename(Integer userid, String tablename);
    boolean existsByUseridAndTablenameAndRefid(Integer userid, String tablename, Integer refid);
}