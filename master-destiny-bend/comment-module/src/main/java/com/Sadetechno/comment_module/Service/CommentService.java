package com.Sadetechno.comment_module.Service;
import com.Sadetechno.comment_module.DTO.CommentRequest;
import com.Sadetechno.comment_module.DTO.CommentResponse;
import com.Sadetechno.comment_module.DTO.UserDTO;
import com.Sadetechno.comment_module.FeignClient.PostFeignClient;
import com.Sadetechno.comment_module.FeignClient.UserFeignClient;
import com.Sadetechno.comment_module.Repository.CommentRepository;
import com.Sadetechno.comment_module.model.Comment;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private PostNotificationService postNotificationService;

    @Autowired
    private PostFeignClient postFeignClient;

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    public CommentResponse createComment(CommentRequest request, MultipartFile file) throws IOException {
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
        Comment comment = new Comment();
        comment.setImagePath(filePath);
        comment.setPostId(request.getPostId());
        comment.setUserId(request.getUserId());
        comment.setRepliedToUserId(request.getRepliedToUserId());
        comment.setTextContent(request.getTextContent());
        comment.setCreatedAt(LocalDateTime.now());

        // Fetch user information for comment userId
        UserDTO userDTO = fetchUserById(request.getUserId());
        String name = userDTO.getName();
        String profileImagePath = userDTO.getProfileImagePath();

        // Check if the comment is a reply to another comment (parentId exists)
        if (request.getParentId() != null) {
            // Fetch parent comment
            Comment parentComment = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));

            comment.setParentComment(parentComment);

            // Set parent ID's user name
            UserDTO parentUserDTO = fetchUserById(parentComment.getUserId());
            comment.setParentIdName(parentUserDTO.getName());

            // Create a reply notification
            String replyMessage = name + " replied to your comment.";
            Long postOwnerId = postFeignClient.getPostWithUserDetails(comment.getPostId()).getUserId();
            postNotificationService.createNotification(
                    comment.getUserId(),
                    replyMessage,
                    parentUserDTO.getEmail(),
                    "COMMENT-REPLY",
                    comment.getPostId(),
                    name,
                    profileImagePath,
                    parentComment.getUserId(),
                    postOwnerId
            );
            logger.info("Reply notification sent to userId: {}", parentComment.getUserId());
        } else {
            // New comment on a post, notify the post owner
            Long postOwnerId = postFeignClient.getPostWithUserDetails(comment.getPostId()).getUserId();
            String commentMessage = name + " commented on your post.";
            postNotificationService.createNotification(
                    comment.getUserId(),
                    commentMessage,
                    userDTO.getEmail(),
                    "POST-COMMENT",
                    comment.getPostId(),
                    name,
                    profileImagePath,
                    null,
                    postOwnerId
            );
            logger.info("Comment notification sent to post owner.");
        }

        // Save the comment and return the response
        Comment savedComment = commentRepository.save(comment);
        return mapToCommentResponse(savedComment);
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


    public List<CommentResponse> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdAndParentCommentIsNull(postId);
        return comments.stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    public List<CommentResponse> getRepliesByParentId(Long parentId) {
        List<Comment> replies = commentRepository.findByParentCommentId(parentId);
        return replies.stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    private CommentResponse mapToCommentResponse(Comment comment) {

        UserDTO userDTO = userFeignClient.getUserById(comment.getUserId());

        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setPostId(comment.getPostId());
        response.setUserId(comment.getUserId());
        response.setRepliedToUserId(comment.getRepliedToUserId());
        response.setTextContent(comment.getTextContent());
        response.setImagePath(comment.getImagePath());
        response.setProfileImagePath(userDTO.getProfileImagePath());
        response.setCreatedAt(comment.getCreatedAt());
        response.setParentIdName(comment.getParentIdName());
        response.setReplies(comment.getReplies().stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toSet()));
        return response;
    }
    public void deleteComment(String commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("User is not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }

    public List<CommentResponse> getCommentsByPostIdOnly(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());
    }


}