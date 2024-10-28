package com.Sadetechno.like_module.Controller;
import com.Sadetechno.like_module.DTO.PostNotificationDTO;
import com.Sadetechno.like_module.DTO.ReelsNotificationDTO;
import com.Sadetechno.like_module.DTO.StatusNotificationDTO;
import com.Sadetechno.like_module.Repository.PostNotificationRepository;
import com.Sadetechno.like_module.Repository.ReelsNotificationRepository;
import com.Sadetechno.like_module.Repository.StatusNotificationRepository;
import com.Sadetechno.like_module.Service.*;
import com.Sadetechno.like_module.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private LikeStatusService likeStatusService;

    @Autowired
    private LikeReelsService likeReelsService;

    @Autowired
    private PostNotificationService postNotificationService;

    @Autowired
    private ReelsNotificationService reelsNotificationService;

    @Autowired
    private StatusNotificationService statusNotificationService;

    @Autowired
    private PostNotificationRepository postNotificationRepository;

    @Autowired
    private StatusNotificationRepository statusNotificationRepository;

    @Autowired
    private ReelsNotificationRepository reelsNotificationRepository;

    private static final Logger logger = LoggerFactory.getLogger(LikeController.class);

    @PostMapping("/toggle")
    public ResponseEntity<?> toggleLike(
            @RequestParam("postId") Long postId,
            @RequestParam("userId") Long userId) {

        Like like = likeService.toggleLike(postId, userId);
        if (like == null) {
            return new ResponseEntity<>("Like removed", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(like, HttpStatus.CREATED);
        }
    }

    @PostMapping("/toggle-status")
    public ResponseEntity<?> toggleLikeForStatus(
            @RequestParam("statusId") Long statusId,
            @RequestParam("userId") Long userId) {

        LikeStatus likeStatus = likeStatusService.toggleLikeStatus(statusId, userId);
        if (likeStatus == null) {
            return new ResponseEntity<>("Like removed", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(likeStatus, HttpStatus.CREATED);
        }
    }
    @PostMapping("/toggle-reels")
    public ResponseEntity<?> toggleLikeForReels(
            @RequestParam("reelsId") Long reelsId,
            @RequestParam("userId") Long userId) {

        LikeReels likeReels = likeReelsService.toggleLikeReels(reelsId, userId);
        if (likeReels == null) {
            return new ResponseEntity<>("Like removed", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(likeReels, HttpStatus.CREATED);
        }
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Like>> getLikesByPostId(@PathVariable Long postId) {
        List<Like> likes = likeService.getLikesByPostId(postId);
        return new ResponseEntity<>(likes, HttpStatus.OK);
    }

    @GetMapping("/post/{postId}/count")
    public ResponseEntity<Long> getLikeCountByPostId(@PathVariable Long postId) {
        long count = likeService.getLikeCountByPostId(postId);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("/post/{postId}/users")
    public ResponseEntity<List<Long>> getUsersWhoLikedPost(@PathVariable Long postId) {
        try {
            List<Long> userIds = likeService.getUserIdsWhoLikedPost(postId);
            logger.info("List of users who liked the post {}",userIds);
            return new ResponseEntity<>(userIds, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error in getting user ids is {}",e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/post/{postId}/user/{userId}")
    public ResponseEntity<Boolean> hasUserLikedPost(
            @PathVariable Long postId,
            @PathVariable Long userId) {

        boolean hasLiked = likeService.hasUserLikedPost(postId, userId);
        return new ResponseEntity<>(hasLiked, HttpStatus.OK);
    }

    @GetMapping("/notification/{postOwnerId}")
    public PostNotificationDTO getNotificationsForUser(@PathVariable Long postOwnerId) {
        List<PostNotification> notifications = postNotificationRepository.findAll().stream()
                .filter(notification -> notification.getPostOwnerId().equals(postOwnerId))
                .sorted(Comparator.comparing(PostNotification::getCreatedAt).reversed())
                .collect(Collectors.toList());

        int count = notifications.size();
        return new PostNotificationDTO(notifications, count);
    }

    @GetMapping("/notification-status/{statusOwnerId}")
    public StatusNotificationDTO getNotificationForStatusUser(@PathVariable Long statusOwnerId){
        List<StatusNotification> statusNotifications = statusNotificationRepository.findAll().stream()
                .filter(statusNotification -> statusNotification.getStatusOwnerId().equals(statusOwnerId))
                .sorted(Comparator.comparing(StatusNotification::getCreatedAt).reversed())
                .collect(Collectors.toList());

        int count = statusNotifications.size();
        return new StatusNotificationDTO(statusNotifications,count);
    }

    @GetMapping("/notification-reels/{reelsOwnerId}")
    public ReelsNotificationDTO getNotificationForReelUser(@PathVariable Long reelsOwnerId){
        List<ReelsNotification> reelsNotifications = reelsNotificationRepository.findAll().stream()
                .filter(reelsNotification -> reelsNotification.getReelsOwnerId().equals(reelsOwnerId))
                .sorted(Comparator.comparing(ReelsNotification::getCreatedAt).reversed())
                .collect(Collectors.toList());

        int count = reelsNotifications.size();
        return new ReelsNotificationDTO(reelsNotifications,count);
    }
    @DeleteMapping("notification/{id}/{type}")
    public ResponseEntity<String> deleteNotification(@PathVariable String id,@PathVariable String type){
        switch (type) {
            case "POST-LIKE" -> postNotificationService.deleteNotificationForPost(id, type);
            case "REEL-LIKE" -> reelsNotificationService.deleteNotificationForReels(id, type);
            case "STATUS-LIKE" -> statusNotificationService.deleteNotificationForStatus(id, type);
            default -> throw new IllegalArgumentException("No id found or type mismatch.");
        }
        return ResponseEntity.ok("Notification deleted");
    }

    @DeleteMapping("/notification/delete-all")
    public ResponseEntity<String> deleteAll(){
        List<PostNotification> notifications = postNotificationRepository.findAll();
        List<ReelsNotification> notifications1 = reelsNotificationRepository.findAll();
        List<StatusNotification> notifications2 = statusNotificationRepository.findAll();

        if(!notifications.isEmpty() && !notifications1.isEmpty() && !notifications2.isEmpty()){
            postNotificationRepository.deleteAll();
            reelsNotificationRepository.deleteAll();
            statusNotificationRepository.deleteAll();
        }else {
            throw new IllegalArgumentException("No notifications to delete.");
        }
        return ResponseEntity.ok("Notifications deleted.");
    }
}
