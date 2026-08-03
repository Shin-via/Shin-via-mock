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
public class MockClientDto {
    private Long clientIdx;
    private String orgCode;
    private String orgName;
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private LocalDateTime createdAt;
}
