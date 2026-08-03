package com.via.shinviamock.connection.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.via.shinviamock.connection.dto.AuthTokenResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Redis 기반 토큰 관리 서비스.
 * Redis 설정이 되어 있으면 Redis DB를 사용하고, 미설정 시 인메모리 저장소로 자동 폴백(Fallback)합니다.
 */
@Slf4j
@Service
public class MockTokenRedisService {

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Redis 연결 불가 또는 미설정 시 사용하는 인메모리 폴백 저장소
    private final Map<String, TokenInfo> accessTokenStore = new ConcurrentHashMap<>();
    private final Map<String, TokenInfo> refreshTokenStore = new ConcurrentHashMap<>();

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenInfo {
        private String accessToken;
        private String refreshToken;
        private String ci;
        private String orgCode;
        private long expiresAt;
        private long refreshExpiresAt;
    }

    /**
     * Access Token & Refresh Token 발급 및 보관
     */
    public AuthTokenResponseDto issueToken(String ci, String orgCode) {
        String ciPrefix = (ci != null && ci.length() > 8) ? ci.substring(0, 8) : (ci != null ? ci : "USER");
        String accessToken = "mock_at_" + ciPrefix + "_" + UUID.randomUUID().toString().replace("-", "");
        String refreshToken = "mock_rt_" + ciPrefix + "_" + UUID.randomUUID().toString().replace("-", "");

        long now = System.currentTimeMillis();
        long accessTokenExpiresIn = 3600; // 1시간
        long refreshTokenExpiresIn = 2592000; // 30일

        TokenInfo tokenInfo = TokenInfo.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .ci(ci)
                .orgCode(orgCode)
                .expiresAt(now + (accessTokenExpiresIn * 1000))
                .refreshExpiresAt(now + (refreshTokenExpiresIn * 1000))
                .build();

        if (redisTemplate != null) {
            try {
                String jsonValue = objectMapper.writeValueAsString(tokenInfo);
                redisTemplate.opsForValue().set(accessToken, jsonValue, accessTokenExpiresIn, TimeUnit.SECONDS);
                redisTemplate.opsForValue().set(refreshToken, jsonValue, refreshTokenExpiresIn, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.warn("Redis 토큰 저장 연동 실패, 인메모리 폴백 저장소를 사용합니다: {}", e.getMessage());
                accessTokenStore.put(accessToken, tokenInfo);
                refreshTokenStore.put(refreshToken, tokenInfo);
            }
        } else {
            accessTokenStore.put(accessToken, tokenInfo);
            refreshTokenStore.put(refreshToken, tokenInfo);
        }

        return AuthTokenResponseDto.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .expiresIn((int) accessTokenExpiresIn)
                .refreshToken(refreshToken)
                .refreshTokenExpiresIn((int) refreshTokenExpiresIn)
                .build();
    }

    /**
     * Refresh Token을 이용한 Access Token 재발급
     */
    public AuthTokenResponseDto refreshAccessToken(String refreshToken, String orgCode) {
        if (refreshToken == null) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 refresh_token입니다.");
        }

        TokenInfo oldTokenInfo = null;

        if (redisTemplate != null) {
            try {
                String jsonValue = redisTemplate.opsForValue().get(refreshToken);
                if (jsonValue != null) {
                    oldTokenInfo = objectMapper.readValue(jsonValue, TokenInfo.class);
                }
            } catch (Exception e) {
                log.warn("Redis 조회 실패, 인메모리 폴백 저장소를 조회합니다: {}", e.getMessage());
            }
        }

        if (oldTokenInfo == null) {
            oldTokenInfo = refreshTokenStore.get(refreshToken);
        }

        if (oldTokenInfo == null) {
            throw new IllegalArgumentException("유효하지 않거나 존재하지 않는 refresh_token입니다.");
        }

        if (orgCode != null && oldTokenInfo.getOrgCode() != null && !orgCode.equals(oldTokenInfo.getOrgCode())) {
            throw new IllegalArgumentException("refresh_token의 기관코드와 일치하지 않습니다.");
        }

        if (System.currentTimeMillis() > oldTokenInfo.getRefreshExpiresAt()) {
            revokeToken(refreshToken);
            throw new IllegalArgumentException("만료된 refresh_token입니다.");
        }

        // 기존 토큰 제거 후 신규 토큰 발급
        revokeToken(oldTokenInfo.getAccessToken());
        revokeToken(refreshToken);

        return issueToken(oldTokenInfo.getCi(), oldTokenInfo.getOrgCode());
    }

    /**
     * Access Token 검증
     */
    public boolean validateAccessToken(String accessToken) {
        if (accessToken == null) return false;
        String cleanToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;

        TokenInfo tokenInfo = null;
        if (redisTemplate != null) {
            try {
                String jsonValue = redisTemplate.opsForValue().get(cleanToken);
                if (jsonValue != null) {
                    tokenInfo = objectMapper.readValue(jsonValue, TokenInfo.class);
                }
            } catch (Exception e) {
                log.warn("Redis 검증 실패, 인메모리 저장소 조회: {}", e.getMessage());
            }
        }

        if (tokenInfo == null) {
            tokenInfo = accessTokenStore.get(cleanToken);
        }

        if (tokenInfo == null) return false;
        return System.currentTimeMillis() <= tokenInfo.getExpiresAt();
    }

    /**
     * Access Token에서 매핑된 CI 추출 (유효하지 않거나 만료 시 null 반환)
     */
    public String getCiByAccessToken(String accessToken) {
        if (accessToken == null) return null;
        String cleanToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;

        TokenInfo tokenInfo = null;
        if (redisTemplate != null) {
            try {
                String jsonValue = redisTemplate.opsForValue().get(cleanToken);
                if (jsonValue != null) {
                    tokenInfo = objectMapper.readValue(jsonValue, TokenInfo.class);
                }
            } catch (Exception e) {
                log.warn("Redis 조회 실패: {}", e.getMessage());
            }
        }

        if (tokenInfo == null) {
            tokenInfo = accessTokenStore.get(cleanToken);
        }

        if (tokenInfo == null || System.currentTimeMillis() > tokenInfo.getExpiresAt()) {
            return null;
        }

        return tokenInfo.getCi();
    }

    /**
     * 토큰 폐기
     */
    public void revokeToken(String token) {
        if (token != null) {
            String cleanToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            if (redisTemplate != null) {
                try {
                    String jsonValue = redisTemplate.opsForValue().get(cleanToken);
                    if (jsonValue != null) {
                        TokenInfo info = objectMapper.readValue(jsonValue, TokenInfo.class);
                        if (info.getAccessToken() != null) redisTemplate.delete(info.getAccessToken());
                        if (info.getRefreshToken() != null) redisTemplate.delete(info.getRefreshToken());
                    } else {
                        redisTemplate.delete(cleanToken);
                    }
                } catch (Exception e) {
                    log.warn("Redis 토큰 삭제 실패: {}", e.getMessage());
                }
            }

            TokenInfo info = accessTokenStore.remove(cleanToken);
            if (info != null) {
                refreshTokenStore.remove(info.getRefreshToken());
            } else {
                refreshTokenStore.remove(cleanToken);
            }
        }
    }
}
