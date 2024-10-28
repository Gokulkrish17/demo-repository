package com.Sadetechno.comment_module.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "reels_comments") // Specify the MongoDB collection name
public class CommentReels {

    @Id
    private String id; // Use String for MongoDB ObjectId

    private Long reelsId;
    private Long userId;

    private String textContent;

    @CreatedDate // Automatically store the creation date
    private LocalDateTime createdAt;
}
