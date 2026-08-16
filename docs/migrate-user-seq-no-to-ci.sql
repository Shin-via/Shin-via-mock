-- mock_openbanking: user_seq_no 기반 식별자를 CI 기반 식별자로 이관
-- 기존 테스트 사용자 1000000001은 TEST_CI_1000000001로 이관한다.

INSERT INTO user_connection (ci, user_name, user_ssn, connected_at)
SELECT 'TEST_CI_1000000001', NULL, NULL, NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM user_connection WHERE ci = 'TEST_CI_1000000001'
);

ALTER TABLE mock_card_bill
    ADD COLUMN ci VARCHAR(100) NULL COMMENT 'OAuth 사용자 식별값',
    ADD KEY idx_mock_card_bill_ci_month (ci, charge_month);

ALTER TABLE mock_bank_account
    ADD COLUMN ci VARCHAR(100) NULL COMMENT 'OAuth 사용자 식별값',
    ADD KEY idx_mock_bank_account_ci_bank (ci, bank_code_tran);

UPDATE mock_card_info
SET ci = CONCAT('TEST_CI_', user_seq_no)
WHERE ci IS NULL OR ci = '';

UPDATE mock_card_bill
SET ci = CONCAT('TEST_CI_', user_seq_no)
WHERE ci IS NULL OR ci = '';

UPDATE mock_bank_account
SET ci = CONCAT('TEST_CI_', user_seq_no)
WHERE ci IS NULL OR ci = '';

ALTER TABLE mock_card_info
    DROP INDEX idx_user_bank,
    DROP COLUMN user_seq_no,
    MODIFY COLUMN ci VARCHAR(100) NOT NULL COMMENT 'OAuth 사용자 식별값';

ALTER TABLE mock_card_bill
    DROP COLUMN user_seq_no,
    MODIFY COLUMN ci VARCHAR(100) NOT NULL COMMENT 'OAuth 사용자 식별값';

ALTER TABLE mock_bank_account
    DROP INDEX idx_mock_bank_account_user_bank,
    DROP COLUMN user_seq_no,
    MODIFY COLUMN ci VARCHAR(100) NOT NULL COMMENT 'OAuth 사용자 식별값';

DROP TABLE mock_oauth_token;
