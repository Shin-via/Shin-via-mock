-- Mock Open Banking: 카드 + OAuth/연결 스키마 (데이터 미포함)
-- 대상 DB: mock_openbanking
USE mock_openbanking;

CREATE TABLE IF NOT EXISTS user_connection (
    connection_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '사용자 연결 식별자',
    ci VARCHAR(100) NULL COMMENT '사용자 CI',
    user_name VARCHAR(100) NULL COMMENT '사용자명',
    user_ssn VARCHAR(255) NULL COMMENT '사용자 식별번호(목 데이터 전용)',
    connected_at DATETIME NULL COMMENT '연결 일시',
    PRIMARY KEY (connection_id),
    KEY idx_user_connection_ci (ci)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='목 OAuth 사용자 연결 정보';

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

CREATE TABLE IF NOT EXISTS mock_card_info (
    card_id VARCHAR(64) NOT NULL,
    ci VARCHAR(100) NOT NULL COMMENT 'OAuth 사용자 식별값',
    bank_code_std VARCHAR(3) NOT NULL,
    card_num_masked VARCHAR(19) NOT NULL,
    card_name VARCHAR(50) NOT NULL,
    card_member_type CHAR(1) NOT NULL,
    issue_date DATE NOT NULL DEFAULT (CURRENT_DATE) COMMENT '카드 발급일자',
    annual_fee BIGINT NOT NULL DEFAULT 0 COMMENT '상품 연회비',
    card_brand VARCHAR(3) NOT NULL DEFAULT '001' COMMENT '카드브랜드 코드',
    linked_bank_code VARCHAR(8) NULL COMMENT '결제은행 코드',
    account_num VARCHAR(20) NULL COMMENT '결제계좌번호',
    is_trans_payable BOOLEAN NOT NULL DEFAULT FALSE COMMENT '교통카드 기능 여부',
    is_cash_card BOOLEAN NOT NULL DEFAULT FALSE COMMENT '현금카드 기능 여부',
    PRIMARY KEY (card_id),
    KEY idx_mock_card_info_ci_bank (ci, bank_code_std)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='목 카드 기본 정보';

CREATE TABLE IF NOT EXISTS mock_card_bill (
    bill_id BIGINT NOT NULL AUTO_INCREMENT,
    card_id VARCHAR(64) NOT NULL,
    ci VARCHAR(100) NOT NULL COMMENT 'OAuth 사용자 식별값',
    charge_month CHAR(6) NOT NULL,
    settlement_seq_no INT NOT NULL,
    charge_amt BIGINT NOT NULL,
    settlement_day TINYINT NOT NULL,
    settlement_date DATE NOT NULL,
    credit_check_type CHAR(2) NOT NULL,
    PRIMARY KEY (bill_id),
    UNIQUE KEY uq_card_month_seq (card_id, charge_month, settlement_seq_no),
    KEY idx_mock_card_bill_ci_month (ci, charge_month),
    CONSTRAINT fk_mock_card_bill_card FOREIGN KEY (card_id) REFERENCES mock_card_info (card_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='목 카드 청구 정보';

CREATE TABLE IF NOT EXISTS mock_card_bill_detail (
    mock_bill_detail_id BIGINT NOT NULL AUTO_INCREMENT,
    card_id VARCHAR(64) NOT NULL,
    charge_month CHAR(6) NOT NULL,
    paid_dtime VARCHAR(14) NOT NULL,
    trans_no VARCHAR(64) NULL,
    paid_amt DECIMAL(18,3) NOT NULL,
    currency_code VARCHAR(3) NULL,
    merchant_name VARCHAR(75) NULL,
    merchant_regno VARCHAR(12) NULL,
    credit_fee_amt BIGINT NOT NULL DEFAULT 0,
    total_install_cnt INT NULL,
    cur_install_cnt INT NULL,
    balance_amt BIGINT NULL,
    prod_type VARCHAR(2) NOT NULL COMMENT '01 일시불/02 신판할부/03 현금서비스/04 리볼빙/05 카드론/06 연회비/99 기타',
    PRIMARY KEY (mock_bill_detail_id),
    KEY idx_mock_bill_detail_card_id (card_id),
    CONSTRAINT fk_mock_bill_detail_card FOREIGN KEY (card_id) REFERENCES mock_card_info (card_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='목 카드 청구 상세';

CREATE TABLE IF NOT EXISTS mock_transactions (
    transaction_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'API 거래 식별자',
    x_api_tran_id VARCHAR(25) NULL COMMENT 'API 거래 ID',
    connection_id BIGINT NULL COMMENT 'OAuth 인가 연결 식별자',
    state VARCHAR(255) NULL COMMENT '요청 state',
    api_url VARCHAR(255) NULL COMMENT '호출 API URL',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    PRIMARY KEY (transaction_id),
    UNIQUE KEY uq_mock_transactions_x_api_tran_id (x_api_tran_id),
    KEY idx_mock_transactions_connection_id (connection_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='목 API 호출 이력';
