package com.Sadetechno.comment_module.Service;

import com.Sadetechno.comment_module.DTO.UserDTO;
import com.Sadetechno.comment_module.FeignClient.ReelsFeignClient;
import com.Sadetechno.comment_module.FeignClient.UserFeignClient;
import com.Sadetechno.comment_module.Repository.CommentReelsRepository;
import com.Sadetechno.comment_module.model.Comment;
import com.Sadetechno.comment_module.model.CommentReels;
import com.Sadetechno.comment_module.model.CommentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentReelsService {

    @Autowired
    private UserFeignClient userFeignClient;

   @Autowired
   private ReelsNotificationService reelsNotificationService;

   @Autowired
   private CommentReelsRepository commentReelsRepository;

   @Autowired
   private ReelsFeignClient reelsFeignClient;

    private static final Logger logger = LoggerFactory.getLogger(CommentReelsService.class);

    public CommentReels createCommentForReels(Long reelsId, Long userId, String textContent) {
        CommentReels commentReels = new CommentReels();
        commentReels.setUserId(userId);
        commentReels.setReelsId(reelsId);
        commentReels.setTextContent(textContent);

        UserDTO userDetail = userFeignClient.getUserById(commentReels.getUserId());
        String message = " commented on your reels.";
        logger.info("Comment sent successfully to reels id {}",reelsId);

        Long reelsOwnerId = reelsFeignClient.getUserDetailsByReelsId(commentReels.getReelsId()).getUserId();
        reelsNotificationService.createNotificationForReels(userId,message, userDetail.getEmail(),"REELS-COMMENT", commentReels.getReelsId(), userDetail.getName(), userDetail.getProfileImagePath(),reelsOwnerId);
        return commentReelsRepository.save(commentReels);
    }

    public List<CommentReels> getCommentForReels(Long reelsId) {
       return commentReelsRepository.findByReelsId(reelsId);
    }

    public Optional<CommentReels> getUserDetailsByCommentId(String id){
        return commentReelsRepository.findById(id);
    }
}
