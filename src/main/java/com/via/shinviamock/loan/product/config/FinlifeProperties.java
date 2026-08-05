package com.via.shinviamock.loan.product.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "external.finlife")
public record FinlifeProperties(
        String baseUrl,
        String authKey,
        String topFinGrpNo
) {
}