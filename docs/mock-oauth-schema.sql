-- Mock OAuth 클라이언트 / 인가 코드 저장소
-- 대상 DB: mock_openbanking

CREATE TABLE IF NOT EXISTS mock_clients (
    client_idx BIGINT NOT NULL AUTO_INCREMENT COMMENT '클라이언트 식별자',
    org_code VARCHAR(10) NOT NULL COMMENT '기관 코드',
    org_name VARCHAR(100) NULL COMMENT '기관명',
    client_id VARCHAR(100) NOT NULL COMMENT 'OAuth client_id',
    client_secret VARCHAR(255) NULL COMMENT 'OAuth client_secret',
    redirect_uri VARCHAR(255) NULL COMMENT '리다이렉트 URI',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    PRIMARY KEY (client_idx),
    UNIQUE KEY uq_mock_clients_client_id (client_id),
    KEY idx_mock_clients_org_code (org_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='목 OAuth 클라이언트';

CREATE TABLE IF NOT EXISTS mock_authorizations (
    connection_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '인가 연결 식별자',
    ci VARCHAR(100) NOT NULL COMMENT '사용자 CI',
    org_code VARCHAR(10) NOT NULL COMMENT '기관 코드',
    code VARCHAR(100) NOT NULL COMMENT '인가 코드',
    app_scheme VARCHAR(255) NULL COMMENT '리다이렉트 URI',
    is_used BOOLEAN NOT NULL DEFAULT FALSE COMMENT '인가 코드 사용 여부',
    expires_at DATETIME NOT NULL COMMENT '만료 일시',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    PRIMARY KEY (connection_id),
    UNIQUE KEY uq_mock_authorizations_code (code),
    KEY idx_mock_authorizations_ci_org (ci, org_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='목 OAuth 인가 코드';
