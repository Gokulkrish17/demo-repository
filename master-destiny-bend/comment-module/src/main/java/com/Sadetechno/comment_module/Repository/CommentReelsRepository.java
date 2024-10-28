package com.Sadetechno.comment_module.Repository;

import com.Sadetechno.comment_module.model.CommentReels;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentReelsRepository extends MongoRepository<CommentReels,String > {
}
