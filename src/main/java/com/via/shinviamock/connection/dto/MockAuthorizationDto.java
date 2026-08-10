package com.via.shinviamock.connection.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockAuthorizationDto {
    private Long connectionId;
    private String ci;
    private String orgCode;
    private String code;
    private String appScheme;
    private Boolean isUsed;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
