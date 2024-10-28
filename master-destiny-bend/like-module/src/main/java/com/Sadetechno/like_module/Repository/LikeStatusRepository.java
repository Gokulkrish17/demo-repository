package com.Sadetechno.like_module.Repository;

import com.Sadetechno.like_module.model.Like;
import com.Sadetechno.like_module.model.LikeStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeStatusRepository extends MongoRepository<LikeStatus,String > {
    Optional<LikeStatus> findByStatusIdAndUserId(Long statusId, Long userId);
}
