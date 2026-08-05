package com.via.shinviamock.loan.product.client;

import com.via.shinviamock.loan.product.config.FinlifeProperties;
import com.via.shinviamock.loan.product.dto.external.credit.CreditResponse;
import com.via.shinviamock.loan.product.dto.external.jeonse.JeonseResponse;
import com.via.shinviamock.loan.product.dto.external.mortgage.MortgageResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;


// 금감원 주택담보대출/전세자금대출/신용대출 API
@Component
public class FinlifeLoanProductClient {

    private final RestClient restClient;
    private final FinlifeProperties properties;
    private final ObjectMapper objectMapper;

    public FinlifeLoanProductClient(
            @Qualifier("finlifeRestClient")
            RestClient restClient,

            FinlifeProperties properties,

            ObjectMapper objectMapper
    ) {
        this.restClient = restClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public MortgageResponse fetchMortgage(int pageNo) {
        return get(
                "/mortgageLoanProductsSearch.json",
                pageNo,
                MortgageResponse.class
        );
    }

    public JeonseResponse fetchJeonse(int pageNo) {
        return get(
                "/rentHouseLoanProductsSearch.json",
                pageNo,
                JeonseResponse.class
        );
    }

    public CreditResponse fetchCredit(int pageNo) {
        return get(
                "/creditLoanProductsSearch.json",
                pageNo,
                CreditResponse.class
        );
    }

    private <T> T get(
            String path,
            int pageNo,
            Class<T> responseType
    ) {
        ResponseEntity<String> response =
                restClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path(path)
                                .queryParam(
                                        "auth",
                                        properties.authKey()
                                )
                                .queryParam(
                                        "topFinGrpNo",
                                        properties.topFinGrpNo()
                                )
                                .queryParam(
                                        "pageNo",
                                        pageNo
                                )
                                .build())
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .toEntity(String.class);

        String body = response.getBody();

        if (response.getStatusCode().is3xxRedirection()) {
            throw new IllegalStateException(
                    "금감원 API 리다이렉트 응답입니다. "
                            + "status="
                            + response.getStatusCode()
                            + ", location="
                            + response.getHeaders().getLocation()
                            + ", path="
                            + path
            );
        }

        if (body == null || body.isBlank()) {
            throw new IllegalStateException(
                    "금감원 API 응답 본문이 비어 있습니다. "
                            + "status="
                            + response.getStatusCode()
                            + ", contentType="
                            + response.getHeaders().getContentType()
                            + ", path="
                            + path
                            + ", pageNo="
                            + pageNo
            );
        }

        try {
            return objectMapper.readValue(
                    body,
                    responseType
            );

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "금감원 API JSON 변환 실패. "
                            + "responseType="
                            + responseType.getSimpleName()
                            + ", status="
                            + response.getStatusCode()
                            + ", contentType="
                            + response.getHeaders().getContentType()
                            + ", body="
                            + abbreviate(body),
                    exception
            );
        }
    }

    private String abbreviate(String body) {
        int maxLength = 500;

        if (body.length() <= maxLength) {
            return body;
        }

        return body.substring(0, maxLength) + "...";
    }
}