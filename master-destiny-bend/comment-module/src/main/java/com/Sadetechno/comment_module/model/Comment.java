package com.Sadetechno.comment_module.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "post_comments") // Specify the MongoDB collection name
public class Comment {

    @Id
    private String id; // Use String for MongoDB ObjectId or keep it Long if you prefer

    private Long postId;
    private Long userId;
    private Long repliedToUserId;

    private String textContent;

    @CreatedDate // This will store the created date automatically
    private LocalDateTime createdAt;

    @DBRef(lazy = true) // Use DBRef for referencing another document
    private Comment parentComment;

    private String parentIdName;

    private String imagePath;

    @DBRef(lazy = true) // Use DBRef for replies if they are stored in a separate collection
    private Set<Comment> replies = new HashSet<>();
}
