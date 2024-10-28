package com.Sadetechno.comment_module.Repository;

import com.Sadetechno.comment_module.model.CommentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentStatusRepository extends MongoRepository<CommentStatus,String > {
}
