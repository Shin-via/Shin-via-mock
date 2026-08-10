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
public class MockTransactionDto {
    private Long transactionId;
    private String xApiTranId;
    private Long connectionId;
    private String state;
    private String apiUrl;
    private LocalDateTime createdAt;
}
