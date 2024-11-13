package com.Sadetechno.comment_module.Service;

import com.Sadetechno.comment_module.DTO.*;
import com.Sadetechno.comment_module.FeignClient.ReelsFeignClient;
import com.Sadetechno.comment_module.FeignClient.UserFeignClient;
import com.Sadetechno.comment_module.Repository.CommentReelsRepository;
import com.Sadetechno.comment_module.model.Comment;
import com.Sadetechno.comment_module.model.CommentReels;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

   @Autowired
   private FileUploadService fileUploadService;

    private static final Logger logger = LoggerFactory.getLogger(CommentReelsService.class);

    public CommentReelsResponse createCommentForReels(CommentReelsRequest request, MultipartFile file) throws IOException {
        String filePath = null;

        // Handle file upload logic
        if (file != null && !file.isEmpty()) {
            filePath = fileUploadService.uploadFile(file);
            String contentType = file.getContentType();
            if (contentType != null && contentType.startsWith("image")) {
                logger.info("Image uploaded successfully.");
            } else {
                throw new IllegalArgumentException("Only image uploads are allowed. Video content is not permitted.");
            }
        } else {
            logger.info("No file uploaded, proceeding without an image.");
        }

        // Create Comment entity
        CommentReels comment = new CommentReels();
        comment.setImagePath(filePath);
        comment.setReelsId(request.getReelsId());
        comment.setUserId(request.getUserId());
        comment.setRepliedToUserId(request.getRepliedToUserId());
        comment.setTextContent(request.getTextContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setName(userFeignClient.getUserById(request.getUserId()).getName());

        // Fetch user information for comment userId
        UserDTO userDTO = fetchUserById(request.getUserId());
        String name = userDTO.getName();
        String profileImagePath = userDTO.getProfileImagePath();

        if (request.getParentId() != null) {
            // The comment is a reply to an existing comment
            CommentReels parentComment = commentReelsRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));

            // Set the parent comment and parent ID name
            comment.setParentComment(parentComment);
            UserDTO parentUserDTO = fetchUserById(parentComment.getUserId());
            comment.setParentIdName(parentUserDTO.getName());

            // Save the reply comment to get its ID generated
            CommentReels savedReply = commentReelsRepository.save(comment);

            // Add the reply to the parent's replies set and save the parent comment
            parentComment.getReplies().add(savedReply);
            commentReelsRepository.save(parentComment);

            // Create a reply notification
            String replyMessage = " replied to your comment.";
            Long reelsOwnerId = reelsFeignClient.getUserDetailsByReelsId(comment.getReelsId()).getUserId();
            reelsNotificationService.createNotificationForReels(
                    comment.getUserId(),
                    replyMessage,
                    userDTO.getEmail(),
                    "REELS-COMMENT",
                    comment.getReelsId(),
                    name,
                    profileImagePath,
                    reelsOwnerId
            );
            logger.info("Reply notification sent to userId: {}", parentComment.getUserId());

        } else {
            // The comment is a new comment on the post
            Long reelsOwnerId = reelsFeignClient.getUserDetailsByReelsId(comment.getReelsId()).getUserId();
            String commentMessage = " commented on your post.";
            reelsNotificationService.createNotificationForReels(
                    comment.getUserId(),
                    commentMessage,
                    userDTO.getEmail(),
                    "REELS-COMMENT",
                    comment.getReelsId(),
                    name,
                    profileImagePath,
                    reelsOwnerId
            );
            logger.info("Comment notification sent to post owner.");

            // Save the new comment directly
            commentReelsRepository.save(comment);
        }

        // Return the response for the created comment
        return mapToCommentReelsResponse(comment);
    }


    /**
     * Fetches the user details using Feign Client and handles possible errors.
     *
     * @param userId The ID of the user to be fetched
     * @return UserDTO The user details
     */
    private UserDTO fetchUserById(Long userId) {
        try {
            return userFeignClient.getUserById(userId);
        } catch (FeignException.NotFound e) {
            logger.error("User not found with ID: {}", userId);
            throw new IllegalArgumentException("User with ID " + userId + " not found.");
        } catch (Exception e) {
            logger.error("Error while fetching user with ID: {}", userId, e);
            throw new RuntimeException("Error fetching user information.");
        }
    }

    private CommentReelsResponse mapToCommentReelsResponse(CommentReels comment) {

        UserDTO userDTO = userFeignClient.getUserById(comment.getUserId());

        CommentReelsResponse response = new CommentReelsResponse();
        response.setId(comment.getId());
        response.setReelsId(comment.getReelsId());
        response.setUserId(comment.getUserId());
        response.setName(userDTO.getName());
        response.setRepliedToUserId(comment.getRepliedToUserId());
        response.setTextContent(comment.getTextContent());
        response.setImagePath(comment.getImagePath());
        response.setProfileImagePath(userDTO.getProfileImagePath());
        response.setCreatedAt(comment.getCreatedAt());
        response.setParentIdName(comment.getParentIdName());
        response.setReplies(comment.getReplies().stream()
                .map(this::mapToCommentReelsResponse)
                .collect(Collectors.toSet()));
        return response;
    }

    public List<CommentReels> getCommentForReels(Long reelsId) {
       return commentReelsRepository.findByReelsId(reelsId);
    }

    public Optional<CommentReels> getUserDetailsByCommentId(String id){
        return commentReelsRepository.findById(id);
    }
}
