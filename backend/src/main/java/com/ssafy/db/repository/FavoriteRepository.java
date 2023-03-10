package com.ssafy.db.repository;

import com.ssafy.db.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUser_id(Long userId);

    List<Favorite> findByLive_id(Long liveId);

    List<Favorite> findByUser_idAndLive_id(Long userId, Long liveId);

}
