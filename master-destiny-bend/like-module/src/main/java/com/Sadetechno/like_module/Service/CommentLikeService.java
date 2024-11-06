package com.Sadetechno.like_module.Service;

import com.Sadetechno.like_module.DTO.UserDTO;
import com.Sadetechno.like_module.FeignClient.CommentPostFeignClient;
import com.Sadetechno.like_module.FeignClient.CommentReelsFeignClient;
import com.Sadetechno.like_module.FeignClient.UserFeignClient;
import com.Sadetechno.like_module.Repository.CommentLikeRepository;
import com.Sadetechno.like_module.model.CommentLike;
import com.Sadetechno.like_module.model.CommentLikeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentLikeService {

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private CommentLikeNotificationService commentLikeNotificationService;

    @Autowired
    private CommentPostFeignClient commentPostFeignClient;

    @Autowired
    private CommentReelsFeignClient commentReelsFeignClient;

    @Autowired
    private UserFeignClient userFeignClient;

    public ResponseEntity<String> toggleLikeForComment(String commentId, Long userId, CommentLikeType commentLikeType){
        Optional<CommentLike> commentLike =  commentLikeRepository.findByCommentIdAndUserId(commentId,userId);
        if(commentLike.isPresent()){
            CommentLike commentLike1 = commentLike.get();
            commentLikeRepository.delete(commentLike1);
            return ResponseEntity.status(HttpStatus.OK).body("Like for the comment is removed");
        }else {
            CommentLike newCommentLike = new CommentLike();
            newCommentLike.setCommentId(commentId);
            newCommentLike.setUserId(userId);
            UserDTO userDTO = userFeignClient.getUserById(userId);
            String userName = userDTO.getName();
            String profileImagePath = userDTO.getProfileImagePath();
            String email = userDTO.getEmail();
            if(String.valueOf(commentLikeType).equals("POST_COMMENT_LIKE") ){
                Long commentOwnerId = commentPostFeignClient.getUserDetailsByCommentIdPost(commentId).getUserId();
                if(!userId.equals(commentOwnerId)){
                    String notificationMessage = "liked your comment.";
                    commentLikeNotificationService.createNotificationForCommentLike(userId, notificationMessage,email,"LIKE_COMMENT",commentId,userName,profileImagePath,commentOwnerId);
                }
            } else if (String.valueOf(commentLikeType).equals("REELS_COMMENT_LIKE")) {
                Long commentOwnerId = commentReelsFeignClient.getUserDetailsByCommentIdReels(commentId).getUserId();
                if(!userId.equals(commentOwnerId)){
                    String notificationMessage = "liked your comment.";
                    commentLikeNotificationService.createNotificationForCommentLike(userId, notificationMessage,email,"LIKE_COMMENT",commentId,userName,profileImagePath,commentOwnerId);
                }
            }

            commentLikeRepository.save(newCommentLike);



            return ResponseEntity.ok("Liked your comment.");
        }
    }
}
