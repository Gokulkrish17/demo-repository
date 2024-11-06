package com.Sadetechno.like_module.Repository;

import com.Sadetechno.like_module.model.CommentLike;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikeRepository extends MongoRepository<CommentLike,String> {
    Optional<CommentLike> findByCommentIdAndUserId(String commentId, Long userId);
}
