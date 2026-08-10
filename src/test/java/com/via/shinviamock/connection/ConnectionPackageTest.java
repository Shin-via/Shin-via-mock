package com.via.shinviamock.connection;

import com.via.shinviamock.connection.dto.AuthTokenResponseDto;
import com.via.shinviamock.connection.dto.CommonResponseDto;
import com.via.shinviamock.connection.dto.MockAuthorizationDto;
import com.via.shinviamock.connection.mapper.MockConnectionMapper;
import com.via.shinviamock.connection.service.MockConnectionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ConnectionPackageTest {

    @Autowired
    private MockConnectionService mockConnectionService;

    @Autowired
    private MockConnectionMapper mockConnectionMapper;

    private final AtomicInteger seq = new AtomicInteger(1000);

    private String generateTranId() {
        return "TR" + (System.currentTimeMillis() % 1000000000L) + seq.getAndIncrement();
    }

    @Test
    @DisplayName("인가코드 발급 -> DB(mock_authorizations, mock_transactions) 저장 검증")
    void generateAuthorizationCodeTest() {
        String ci = "TEST_USER_CI_12345";
        String clientId = "Shinvia_client_id";
        String redirectUri = "http://Shinvia";
        String state = "test_state_123";
        String tranId = generateTranId();

        String mockCode = mockConnectionService.generateAuthorizationCode(ci, clientId, "ShinVia", redirectUri, state, tranId);

        assertThat(mockCode).isNotNull().startsWith("MOCK_CODE_");

        MockAuthorizationDto auth = mockConnectionMapper.selectAuthorizationByCode(mockCode);
        assertThat(auth).isNotNull();
        assertThat(auth.getCi()).isEqualTo(ci);
        assertThat(auth.getOrgCode()).isEqualTo("ShinVia");
        assertThat(auth.getIsUsed()).isFalse();
    }

    @Test
    @DisplayName("인가코드로 토큰 발급 -> is_used 상태 변경 및 TokenResponse 반환 검증")
    void issueTokenByCodeTest() {
        String ci = "TEST_USER_CI_67890";
        String authTranId = generateTranId();
        String tokenTranId = generateTranId();

        String mockCode = mockConnectionService.generateAuthorizationCode(ci, "Shinvia_client_id", "ShinVia", "http://Shinvia", "state", authTranId);

        AuthTokenResponseDto response = mockConnectionService.issueTokenByCode(tokenTranId, "ShinVia", "authorization_code", mockCode, "Shinvia_client_id", "secret", "http://Shinvia");

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getRefreshToken()).isNotNull();

        MockAuthorizationDto auth = mockConnectionMapper.selectAuthorizationByCode(mockCode);
        assertThat(auth.getIsUsed()).isTrue();
    }

    @Test
    @DisplayName("Refresh Token으로 토큰 재발급 검증")
    void issueTokenByRefreshTokenTest() {
        String ci = "TEST_USER_CI_11111";
        String authTranId = generateTranId();
        String tokenTranId = generateTranId();
        String refreshTranId = generateTranId();

        String mockCode = mockConnectionService.generateAuthorizationCode(ci, "Shinvia_client_id", "ShinVia", "http://Shinvia", "state", authTranId);
        AuthTokenResponseDto initialTokens = mockConnectionService.issueTokenByCode(tokenTranId, "ShinVia", "authorization_code", mockCode, "Shinvia_client_id", "secret", "http://Shinvia");

        AuthTokenResponseDto reissuedTokens = mockConnectionService.issueTokenByRefreshToken(refreshTranId, "ShinVia", "refresh_token", initialTokens.getRefreshToken(), "Shinvia_client_id", "secret", "N");

        assertThat(reissuedTokens).isNotNull();
        assertThat(reissuedTokens.getAccessToken()).isNotNull();
        assertThat(reissuedTokens.getAccessToken()).isNotEqualTo(initialTokens.getAccessToken());
    }

    @Test
    @DisplayName("토큰 폐기(revoke) 검증")
    void revokeTokenTest() {
        String tranId = generateTranId();
        CommonResponseDto response = mockConnectionService.revokeToken("mock_at_TEST_USER_123", "Shinvia_client_id", "secret", "ShinVia", "0", tranId);

        assertThat(response.getRspCode()).isEqualTo("00000");
        assertThat(response.getRspMsg()).isEqualTo("성공");
    }
}
