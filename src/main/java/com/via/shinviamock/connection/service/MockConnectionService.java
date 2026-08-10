package com.via.shinviamock.connection.service;

import com.via.shinviamock.connection.dto.*;
import com.via.shinviamock.connection.mapper.MockConnectionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockConnectionService {

    private final MockConnectionMapper mockConnectionMapper;
    private final MockTokenRedisService mockTokenRedisService;
    private final RedisTemplate redisTemplate;

    /**
     * 1. 인가 코드 발급 및 DB (mock_authorizations, mock_transactions) 저장
     */
    @Transactional
    public String generateAuthorizationCode(String ci, String clientId, String orgCode, String redirectUri, String state, String tranId) {
        if (clientId == null || clientId.trim().isEmpty()) {
            throw new IllegalArgumentException("client_id(클라이언트 아이디)가 필요합니다.");
        }
        if (orgCode == null || orgCode.trim().isEmpty()) {
            throw new IllegalArgumentException("org_code(기관코드)가 필요합니다.");
        }

        MockClientDto client = mockConnectionMapper.selectClientByClientId(clientId.trim());
        if (client == null) {
            throw new IllegalArgumentException("등록되지 않은 client_id입니다.");
        }
        if (!orgCode.trim().equals(client.getOrgCode())) {
            throw new IllegalArgumentException("클라이언트 아이디와 기관코드가 일치하지 않습니다.");
        }

        String mockCode = "MOCK_CODE_" + UUID.randomUUID().toString().replace("-", "");

        // 1-1. mock_authorizations 레코드 저장
        MockAuthorizationDto auth = MockAuthorizationDto.builder()
                .ci(ci)
                .orgCode(client.getOrgCode())
                .code(mockCode)
                .appScheme(redirectUri)
                .isUsed(false)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

        mockConnectionMapper.insertAuthorization(auth);

        // 1-2. mock_transactions 이력 저장
        MockTransactionDto transaction = MockTransactionDto.builder()
                .xApiTranId(resolveUniqueTranId(tranId))
                .connectionId(auth.getConnectionId())
                .state(state)
                .apiUrl("/v2/oauth/2.0/authorize")
                .build();

        mockConnectionMapper.insertTransaction(transaction);

        return mockCode;
    }

    /**
     * 2. 인가코드로 Access / Refresh Token 발급 (Redis 연동 및 mock_transactions 기록)
     */
    @Transactional
    public AuthTokenResponseDto issueTokenByCode(String tranId,String orgCode, String grantType,String code, String clientId, String clientSecret, String redirectUri ) {
        //                                          (tranId,orgCode, grantType, code, clientId, clientSecret,redirectUri);
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("code(인가코드)가 필요합니다.");
        }
       // if(refre)
        if (clientId == null || clientId.trim().isEmpty()) {
            throw new IllegalArgumentException("client_id(클라이언트 아이디)가 필요합니다.");
        }
        if (orgCode == null || orgCode.trim().isEmpty()) {
            throw new IllegalArgumentException("org_code(기관코드)가 필요합니다.");
        }

        if (redirectUri == null || redirectUri.trim().isEmpty()) {
            throw new IllegalArgumentException("redirect_uri가 필요합니다.");
        }

        // 1. mock_clients 검증
        MockClientDto client = mockConnectionMapper.selectClientByClientId(clientId.trim());
        if (client == null) {
            throw new IllegalArgumentException("등록되지 않은 client_id입니다.");
        }
        if (!orgCode.trim().equals(client.getOrgCode())) {
            throw new IllegalArgumentException("클라이언트 아이디와 기관코드가 일치하지 않습니다.");
        }

        // 2. 인가코드 존재 및 일치 검증
        MockAuthorizationDto auth = mockConnectionMapper.selectAuthorizationByCode(code.trim());
        if (auth == null) {
            throw new IllegalArgumentException("유효하지 않거나 존재하지 않는 인가코드입니다.");
        }

        if (!orgCode.trim().equals(auth.getOrgCode())) {
            throw new IllegalArgumentException("요청한 기관코드가 인가코드 발급 당시의 기관코드와 일치하지 않습니다.");
        }

        if (auth.getAppScheme() != null && !redirectUri.trim().equals(auth.getAppScheme())) {
            throw new IllegalArgumentException("redirect_uri가 인가코드 발급 당시의 redirect_uri와 일치하지 않습니다.");
        }

        if (Boolean.TRUE.equals(auth.getIsUsed())) {
            throw new IllegalArgumentException("이미 사용된 인가코드입니다.");
        }

        if (auth.getExpiresAt() != null && auth.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("만료된 인가코드입니다.");
        }

        // 인가코드 사용 완료 처리 (is_used = true)
        mockConnectionMapper.updateAuthorizationUsed(auth.getConnectionId(), auth.getCode());

        // Redis(토큰 저장소)에서 Access Token & Refresh Token 생성/저장
        AuthTokenResponseDto tokenResponse = mockTokenRedisService.issueToken(auth.getCi(), auth.getOrgCode());

        // mock_transactions 이력 저장
        MockTransactionDto transaction = MockTransactionDto.builder()
                .xApiTranId(resolveUniqueTranId(tranId))
                .connectionId(auth.getConnectionId())
                .apiUrl("/v2/oauth/2.0/token")
                .build();

        mockConnectionMapper.insertTransaction(transaction);

        return tokenResponse;
    }

    /**
     * 3. Refresh Token으로 Access Token 재발급
     */
    @Transactional
    public AuthTokenResponseDto issueTokenByRefreshToken(String tranId, String orgCode, String grantType, String refreshToken,  String clientId, String clientSecret,String isRefreshed)
    {
        String result;
        if(redisTemplate.hasKey(refreshToken)){
            result = "good";
        }else {
            result = "bad";
        }
        log.info("refreshToken" + refreshToken+ " " +result);
        if (refreshToken == null || refreshToken.trim().isEmpty() && redisTemplate.hasKey("mydata:ci:at:" + refreshToken)) {
            throw new IllegalArgumentException("refresh_token이 필요합니다.");
        }
        if (clientId == null || clientId.trim().isEmpty()) {
            throw new IllegalArgumentException("client_id(클라이언트 아이디)가 필요합니다.");
        }
        if (clientSecret == null || clientSecret.trim().isEmpty()) {
            throw new IllegalArgumentException("client_secret이 필요합니다.");
        }
        if (orgCode == null || orgCode.trim().isEmpty()) {
            throw new IllegalArgumentException("org_code(기관코드)가 필요합니다.");
        }
        if (isRefreshed == null || isRefreshed.trim().isEmpty()) {
            throw new IllegalArgumentException("is_refreshed(토큰 갱신 여부)가 필요합니다.");
        }

        if (!"N".equalsIgnoreCase(isRefreshed.trim())) {
            throw new IllegalArgumentException("is_refreshed(토큰 갱신 여부)는 'N'이어야 접근이 가능합니다.");
        }

        // 1. mock_clients DB 검증
        MockClientDto client = mockConnectionMapper.selectClientByClientId(clientId.trim());
        if (client == null) {
            throw new IllegalArgumentException("등록되지 않은 client_id입니다.");
        }
        if (!orgCode.trim().equals(client.getOrgCode())) {
            throw new IllegalArgumentException("클라이언트 아이디와 기관코드가 일치하지 않습니다.");
        }
        if (client.getClientSecret() != null && !client.getClientSecret().isBlank()
                && !clientSecret.trim().equals(client.getClientSecret().trim())) {
            // 목 서버 테스트 편의성을 위해 통과 처리
        }

        // 2. Refresh Token 검증 및 토큰 재발급
        AuthTokenResponseDto tokenResponse = mockTokenRedisService.refreshAccessToken(refreshToken.trim(), orgCode.trim());

        MockTransactionDto transaction = MockTransactionDto.builder()
                .xApiTranId(resolveUniqueTranId(tranId))
                .apiUrl("/v2/oauth/2.0/token (refresh)")
                .build();

        mockConnectionMapper.insertTransaction(transaction);

        return tokenResponse;
    }

    /**
     * 4. 토큰 폐기
     */
    @Transactional
    public CommonResponseDto revokeToken(String token, String clientId, String clientSecret, String orgCode, String revokeType, String tranId) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("token이 필요합니다.");
        }
        if (clientId == null || clientId.trim().isEmpty()) {
            throw new IllegalArgumentException("client_id(클라이언트 아이디)가 필요합니다.");
        }
        if (clientSecret == null || clientSecret.trim().isEmpty()) {
            throw new IllegalArgumentException("client_secret이 필요합니다.");
        }
        if (orgCode == null || orgCode.trim().isEmpty()) {
            throw new IllegalArgumentException("org_code(기관코드)가 필요합니다.");
        }

        // 1. mock_clients DB 검증
        MockClientDto client = mockConnectionMapper.selectClientByClientId(clientId.trim());
        if (client == null) {
            throw new IllegalArgumentException("등록되지 않은 client_id입니다.");
        }
        if (!orgCode.trim().equals(client.getOrgCode())) {
            throw new IllegalArgumentException("클라이언트 아이디와 기관코드가 일치하지 않습니다.");
        }
        if (client.getClientSecret() != null && !client.getClientSecret().isBlank()
                && !clientSecret.trim().equals(client.getClientSecret().trim())) {
        }

        // 2. 토큰 폐기 (revoke_type은 더미 파라미터로 무시)
        mockTokenRedisService.revokeToken(token.trim());

        MockTransactionDto transaction = MockTransactionDto.builder()
                .xApiTranId(resolveUniqueTranId(tranId))
                .apiUrl("/v2/oauth/2.0/revoke")
                .build();

        mockConnectionMapper.insertTransaction(transaction);

        return new CommonResponseDto("00000", "성공");
    }

    private String resolveUniqueTranId(String tranId) {
        if (tranId == null || tranId.isBlank()) {
            return "TR" + System.currentTimeMillis() + (int) (Math.random() * 1000);
        }
        if (mockConnectionMapper.countTransactionByTranId(tranId) > 0) {
            String suffix = "_" + (int) (Math.random() * 9000 + 1000);
            if (tranId.length() + suffix.length() <= 25) {
                return tranId + suffix;
            } else {
                return tranId.substring(0, 25 - suffix.length()) + suffix;
            }
        }
        return tranId;
    }



}
