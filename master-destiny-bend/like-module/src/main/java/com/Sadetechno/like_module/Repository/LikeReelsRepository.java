package com.Sadetechno.like_module.Repository;

import com.Sadetechno.like_module.model.LikeReels;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeReelsRepository extends MongoRepository<LikeReels,String > {
    Optional<LikeReels> findByReelsIdAndUserId(Long reelsId, Long userId);
}
