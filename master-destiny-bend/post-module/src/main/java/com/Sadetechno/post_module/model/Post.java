package com.Sadetechno.post_module.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;


@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Enumerated(EnumType.STRING)
    private PostType postType;

    private  String description;
    private String imageUrl;
    private String videoUrl;

    @Enumerated(EnumType.STRING)
    private PostVisibility postVisibility;

    @Enumerated(EnumType.STRING)
    private PrivacySetting privacySetting;  // New field for privacy settings

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Column(name = "user_id")
    private Long userId;
}