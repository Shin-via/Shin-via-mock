-- Mock OAuth 사용자 연결 / 카드 CI / API 거래 이력
-- 대상 DB: mock_openbanking

CREATE TABLE IF NOT EXISTS user_connection (
    connection_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '사용자 연결 식별자',
    ci VARCHAR(100) NULL COMMENT '사용자 CI',
    user_name VARCHAR(100) NULL COMMENT '사용자명',
    user_ssn VARCHAR(255) NULL COMMENT '사용자 식별번호(목 데이터 전용)',
    connected_at DATETIME NULL COMMENT '연결 일시',
    PRIMARY KEY (connection_id),
    KEY idx_user_connection_ci (ci)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='목 OAuth 사용자 연결 정보';

ALTER TABLE mock_card_info
    ADD COLUMN ci VARCHAR(100) NULL COMMENT 'OAuth 사용자 식별값',
    ADD KEY idx_mock_card_info_ci_bank (ci, bank_code_std);

CREATE TABLE IF NOT EXISTS mock_transactions (
    transaction_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'API 거래 식별자',
    x_api_tran_id VARCHAR(25) NULL COMMENT 'API 거래 ID',
    connection_id BIGINT NULL COMMENT 'OAuth 연결 식별자',
    state VARCHAR(255) NULL COMMENT '요청 state',
    api_url VARCHAR(255) NULL COMMENT '호출 API URL',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    PRIMARY KEY (transaction_id),
    UNIQUE KEY uq_mock_transactions_x_api_tran_id (x_api_tran_id),
    KEY idx_mock_transactions_connection_id (connection_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='목 API 호출 이력';
