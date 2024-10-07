package com.Sadetechno.comment_module.DTO;

import com.Sadetechno.comment_module.model.ReelsNotification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReelsNotificationDTO {

    private List<ReelsNotification> notifications;
    private int count;
}