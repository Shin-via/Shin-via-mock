package com.via.shinviamock.connection.service;

import com.via.shinviamock.connection.dto.AuthTokenResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Redis 기반 토큰 관리 서비스.
 * Key: at:{ci} -> Value: accessToken 문자열 (1시간 TTL)
 * Key: rt:{ci} -> Value: refreshToken 문자열 (1년 TTL)
 */
@Service
public class MockTokenRedisService {

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    // Redis 미연동 시 사용하는 인메모리 폴백 저장소 (ci -> token)
    private final Map<String, String> atStore = new ConcurrentHashMap<>();
    private final Map<String, String> rtStore = new ConcurrentHashMap<>();

    /**
     * Access Token & Refresh Token 발급 및 보관 (1 CI당 1개 토큰 유지)
     */
    public AuthTokenResponseDto issueToken(String ci, String orgCode) {
        String effectiveCi = ci ;

        String accessToken = "mock_at_" + effectiveCi + "_" + UUID.randomUUID().toString().replace("-", "");
        String refreshToken = "mock_rt_" + effectiveCi + "_" + UUID.randomUUID().toString().replace("-", "");

        long accessTokenExpiresIn = 3600; // 1시간 (3,600초)
        long refreshTokenExpiresIn = 31536000; // 1년 (365일 = 31,536,000초)

        if (redisTemplate != null) {
            try {
                // 기존 CI에 발급된 토큰이 있는 경우, 기존 토큰의 역방향 매핑 키(mydata:at:ci:{oldAt}, mydata:rt:ci:{oldRt}) 및 구 키(at:{ci}, rt:{ci}) 삭제
                String oldAccessToken = redisTemplate.opsForValue().get("mydata:ci:at:" + effectiveCi);
                String oldRefreshToken = redisTemplate.opsForValue().get("mydata:ci:rt:" + effectiveCi);
                if (oldAccessToken != null) {
                    redisTemplate.delete("mydata:at:ci:" + oldAccessToken);
                }
                if (oldRefreshToken != null) {
                    redisTemplate.delete("mydata:rt:ci:" + oldRefreshToken);
                }
                redisTemplate.delete("at:" + effectiveCi);
                redisTemplate.delete("rt:" + effectiveCi);

                // 양방향 4개 Key-Value 저장 (TTL: Access 1시간, Refresh 1년)
                // 1) ci : accesstoken
                redisTemplate.opsForValue().set("mydata:ci:at:" + effectiveCi, accessToken, accessTokenExpiresIn, TimeUnit.SECONDS);
                // 2) ci : refreshtoken
                redisTemplate.opsForValue().set("mydata:ci:rt:" + effectiveCi, refreshToken, refreshTokenExpiresIn, TimeUnit.SECONDS);
                // 3) accesstoken : ci
                redisTemplate.opsForValue().set("mydata:at:ci:" + accessToken, effectiveCi, accessTokenExpiresIn, TimeUnit.SECONDS);
                // 4) refreshtoken : ci
                redisTemplate.opsForValue().set("mydata:rt:ci:" + refreshToken, effectiveCi, refreshTokenExpiresIn, TimeUnit.SECONDS);
            } catch (Exception e) {
                atStore.put(effectiveCi, accessToken);
                rtStore.put(effectiveCi, refreshToken);
            }
        } else {
            atStore.put(effectiveCi, accessToken);
            rtStore.put(effectiveCi, refreshToken);
        }

        return AuthTokenResponseDto.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .expiresIn((int) accessTokenExpiresIn)
                .refreshToken(refreshToken)
                .refreshTokenExpiresIn((int) refreshTokenExpiresIn)
                .build();
    }

     //Refresh Token을 이용한 Access Token 재발급
    public AuthTokenResponseDto refreshAccessToken(String refreshToken, String orgCode) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 refresh_token입니다.");
        }

        String cleanToken = refreshToken.startsWith("Bearer ") ? refreshToken.substring(7).trim() : refreshToken.trim();
        String ci = null;

        if (redisTemplate != null) {
            try {
                ci = redisTemplate.opsForValue().get("mydata:rt:ci:" + cleanToken);
            } catch (Exception e) {
            }
        }

        if (ci == null) {
            ci = extractCiFromToken(cleanToken);
            if (ci != null) {
                String storedRt = rtStore.get(ci);
                if (storedRt != null && !storedRt.equals(cleanToken)) {
                    ci = null;
                }
            }
        }

        if (ci == null || ci.isBlank()) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 refresh_token입니다.");
        }

        // 새 토큰 발급 (issueToken 호출 시 기존 토큰 역방향 매핑 삭제 및 4개 키 신규 저장)
        return issueToken(ci, orgCode);
    }


     // Access Token 검증
    public boolean validateAccessToken(String accessToken) {
        return getCiByAccessToken(accessToken) != null;
    }

     // Access Token에서 매핑된 CI 추출 (유효하지 않거나 만료 시 null 반환)
    public String getCiByAccessToken(String accessToken) {
        if (accessToken == null) return null;
        String cleanToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;

        if (redisTemplate != null) {
            try {
                String ci = redisTemplate.opsForValue().get("mydata:at:ci:" + cleanToken);
                if (ci != null && !ci.isBlank()) {
                    return ci;
                }
            } catch (Exception e) {
            }
        }

        String ci = extractCiFromToken(cleanToken);
        if (ci != null) {
            String storedAt = atStore.get(ci);
            if (storedAt != null && storedAt.equals(cleanToken)) {
                return ci;
            }
        }

        return null;
    }

    //토큰 폐기 (Access Token만 삭제 / Refresh Token은 유지)
    public void revokeToken(String token) {
        if (token == null) return;
        String cleanToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        String ci = extractCiFromToken(cleanToken);

        if (cleanToken.startsWith("mock_at_")) {
            // Access Token만 삭제
            if (redisTemplate != null) {
                try {
                    if (ci != null) {
                        redisTemplate.delete("mydata:ci:at:" + ci);
                        redisTemplate.delete("at:" + ci);
                    }
                    redisTemplate.delete("mydata:at:ci:" + cleanToken);
                } catch (Exception e) {
                }
            }
            if (ci != null) {
                atStore.remove(ci);
            }
        } else {
            // Refresh Token이거나 전체 폐기인 경우 전체 삭제
            if (ci != null) {
                revokeTokenByCi(ci);
            } else if (redisTemplate != null) {
                try {
                    redisTemplate.delete("mydata:rt:ci:" + cleanToken);
                } catch (Exception e) {
                }
            }
        }
    }

    //토큰 폐기후 레디스에서 데이터 삭제 (전체 삭제)
    public void revokeTokenByCi(String ci) {
        if (ci == null) return;

        if (redisTemplate != null) {
            try {
                String oldAccessToken = redisTemplate.opsForValue().get("mydata:ci:at:" + ci);
                String oldRefreshToken = redisTemplate.opsForValue().get("mydata:ci:rt:" + ci);

                if (oldAccessToken != null) redisTemplate.delete("mydata:at:ci:" + oldAccessToken);
                if (oldRefreshToken != null) redisTemplate.delete("mydata:rt:ci:" + oldRefreshToken);

                redisTemplate.delete("mydata:ci:at:" + ci);
                redisTemplate.delete("mydata:ci:rt:" + ci);
                redisTemplate.delete("at:" + ci);
                redisTemplate.delete("rt:" + ci);
            } catch (Exception e) {
            }
        }

        atStore.remove(ci);
        rtStore.remove(ci);
    }

    //토큰으로 ci 추출
    private String extractCiFromToken(String token) {
        if (token == null) return null;
        if (token.startsWith("mock_rt_") || token.startsWith("mock_at_")) {
            int prefixLen = 8; // "mock_at_" 또는 "mock_rt_"
            int uuidLen = 33;  // "_" + 32자리 UUID
            if (token.length() >= prefixLen + uuidLen) {
                return token.substring(prefixLen, token.length() - uuidLen);
            }
        }
        return null;
    }
}
