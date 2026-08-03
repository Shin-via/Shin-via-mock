package com.via.shinviamock.connection.controller;

import com.via.shinviamock.connection.dto.AuthTokenResponseDto;
import com.via.shinviamock.connection.dto.CommonResponseDto;
import com.via.shinviamock.connection.service.MockConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/v2/oauth/2.0")
@RequiredArgsConstructor
public class MockConnectionController {

    private final MockConnectionService mockConnectionService;

    /**
     * 1. 인가 코드 발급 요청 (GET /v2/oauth/2.0/authorize)
     */
    @GetMapping("/authorize")
    public ResponseEntity<?> authorize(
            @RequestHeader(value = "x-user-ci", required = false) String userCi,
            @RequestHeader(value = "x-api-tran-id", required = false, defaultValue = "MOCK_TRAN_1234567890") String tranId,
            @RequestParam(value = "response_type", required = false, defaultValue = "code") String responseType,
            @RequestParam(value = "client_id", required = false) String clientId,
            @RequestParam(value = "redirect_uri") String redirectUri,
            @RequestParam(value = "org_code", required = false) String orgCode,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "app_scheme", required = false) String appScheme) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-tran-id", tranId);

        if (userCi == null || userCi.trim().isEmpty()) {
            return ResponseEntity.badRequest().headers(headers).body(new CommonResponseDto("40000", "x-user-ci 헤더가 필요합니다."));
        }

        try {
            // DB (mock_authorizations, mock_transactions)에 인가코드 및 거래 이력 생성 (client_id, org_code 검증 및 redirect_uri 저장)
            String mockCode = mockConnectionService.generateAuthorizationCode(userCi, clientId, orgCode, redirectUri, state, tranId);

            // Redirect URL 생성 (Query Parameter에 code, state, api_tran_id 포함)
            String targetUrl = String.format("%s?code=%s&state=%s&api_tran_id=%s",
                    redirectUri, mockCode, state != null ? state : "", tranId);

            headers.setLocation(URI.create(targetUrl));

            // 302 Found로 Redirect 응답
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().headers(headers).body(new CommonResponseDto("40001", e.getMessage()));
        }
    }

    /**
     * 2. 접근 토큰 발급 및 갱신 요청 (POST /v2/oauth/2.0/token)
     * Content-Type: application/x-www-form-urlencoded
     */
    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> issueToken(
            @RequestHeader(value = "x-api-tran-id", required = false, defaultValue = "MOCK_TRAN_1234567890") String tranId,
            @RequestParam(value = "grant_type", required = false) String grantType,
            @RequestParam(value = "client_id", required = false) String clientId,
            @RequestParam(value = "client_secret", required = false) String clientSecret,
            @RequestParam(value = "org_code", required = false) String orgCode,
            @RequestParam(value = "redirect_uri", required = false) String redirectUri,
            @RequestParam(value = "is_refreshed", required = false) String isRefreshed,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "refresh_token", required = false) String refreshToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-tran-id", tranId);

        if (grantType == null || grantType.trim().isEmpty()) {
            return ResponseEntity.badRequest().headers(headers).body(new CommonResponseDto("40000", "grant_type(발급 유형)은 필수 항목입니다."));
        }

        try {
            if ("authorization_code".equals(grantType.trim())) {
                AuthTokenResponseDto response = mockConnectionService.issueTokenByCode(code, clientId, orgCode, redirectUri, tranId);
                return ResponseEntity.ok().headers(headers).body(response);
            } else if ("refresh_token".equals(grantType.trim())) {
                AuthTokenResponseDto response = mockConnectionService.issueTokenByRefreshToken(refreshToken, clientId, clientSecret, orgCode, isRefreshed, tranId);
                return ResponseEntity.ok().headers(headers).body(response);
            } else {
                return ResponseEntity.badRequest().headers(headers).body(new CommonResponseDto("40000", "지원하지 않는 grant_type입니다. (" + grantType + ")"));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().headers(headers).body(new CommonResponseDto("40001", e.getMessage()));
        }
    }

    /**
     * 3. 접근 토큰 폐기 요청 (POST /v2/oauth/2.0/revoke)
     */
    @PostMapping(value = "/revoke", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> revokeToken(
            @RequestHeader(value = "x-api-tran-id", required = false, defaultValue = "MOCK_TRAN_1234567890") String tranId,
            @RequestParam(value = "token", required = false) String token,
            @RequestParam(value = "client_id", required = false) String clientId,
            @RequestParam(value = "client_secret", required = false) String clientSecret,
            @RequestParam(value = "org_code", required = false) String orgCode,
            @RequestParam(value = "revoke_type", required = false) String revokeType) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-tran-id", tranId);

        try {
            CommonResponseDto response = mockConnectionService.revokeToken(token, clientId, clientSecret, orgCode, revokeType, tranId);
            return ResponseEntity.ok().headers(headers).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().headers(headers).body(new CommonResponseDto("40001", e.getMessage()));
        }
    }
}
