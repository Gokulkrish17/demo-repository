package com.Sadetechno.jwt_module.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Document(collection = "otp_list")
public class OtpEntity {

    @Id
    private String id;

    private String email;
    private String otp;
    private LocalDateTime createdAt;

    public OtpEntity(String email, String otp, LocalDateTime createdAt) {
        this.email = email;
        this.otp = otp;
        this.createdAt = createdAt;
    }
}
