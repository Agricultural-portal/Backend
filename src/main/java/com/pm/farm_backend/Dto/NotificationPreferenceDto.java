package com.pm.farm_backend.Dto;

import com.pm.farm_backend.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferenceDto {
    private Long id;
    private Long userId;
    private NotificationType type;
    private Boolean enableInApp;
    private Boolean enableEmail;
    private Boolean enableSms;
}
