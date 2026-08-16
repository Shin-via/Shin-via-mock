-- 기존 테스트 사용자(TEST_CI_1000000001)용 신비아 심플 카드 추가 데이터
-- 기존 데이터는 변경하지 않고 CARD_TEST_USER1_001 및 그 청구/상세 내역만 추가한다.

SET NAMES utf8mb4;

INSERT INTO mock_card_info (
    card_id, bank_code_std, card_num_masked, card_name, card_member_type,
    issue_date, annual_fee, card_brand, linked_bank_code, account_num,
    is_trans_payable, is_cash_card, ci
) VALUES (
    'CARD_TEST_USER1_001', '004', '9876-54**-****-3210', '신비아 심플 카드', '1',
    '2026-01-15', 0, '001', '004', '11012345678901234567',
    FALSE, FALSE, 'TEST_CI_1000000001'
)
ON DUPLICATE KEY UPDATE card_name = VALUES(card_name);

INSERT INTO mock_card_bill (
    card_id, charge_month, settlement_seq_no, charge_amt, settlement_day,
    settlement_date, credit_check_type, ci
) VALUES
    ('CARD_TEST_USER1_001', '202605', 0,  48500, 25, '2026-05-25', '01', 'TEST_CI_1000000001'),
    ('CARD_TEST_USER1_001', '202606', 0,  71200, 25, '2026-06-25', '01', 'TEST_CI_1000000001'),
    ('CARD_TEST_USER1_001', '202607', 0,  63800, 25, '2026-07-25', '01', 'TEST_CI_1000000001');

INSERT INTO mock_card_bill_detail (
    card_id, charge_month, paid_dtime, trans_no, paid_amt, currency_code,
    merchant_name, merchant_regno, credit_fee_amt, total_install_cnt,
    cur_install_cnt, balance_amt, prod_type
) VALUES
    ('CARD_TEST_USER1_001', '202605', '20260504123500', 'SIMPLE-202605-001', 12500.000, 'KRW', '동네마트',       '2012345678', 0, NULL, NULL, NULL, '01'),
    ('CARD_TEST_USER1_001', '202605', '20260515181000', 'SIMPLE-202605-002', 18000.000, 'KRW', '커피전문점',     '2023456789', 0, NULL, NULL, NULL, '01'),
    ('CARD_TEST_USER1_001', '202605', '20260527194000', 'SIMPLE-202605-003', 18000.000, 'KRW', '온라인서점',     '2024567890', 0, NULL, NULL, NULL, '01'),
    ('CARD_TEST_USER1_001', '202606', '20260603132000', 'SIMPLE-202606-001', 22000.000, 'KRW', '편의점',         '2025678901', 0, NULL, NULL, NULL, '01'),
    ('CARD_TEST_USER1_001', '202606', '20260614191000', 'SIMPLE-202606-002', 31200.000, 'KRW', '음식점',         '2026789012', 0, NULL, NULL, NULL, '01'),
    ('CARD_TEST_USER1_001', '202606', '20260625172000', 'SIMPLE-202606-003', 18000.000, 'KRW', '영화관',         '2027890123', 0, NULL, NULL, NULL, '01'),
    ('CARD_TEST_USER1_001', '202607', '20260707124500', 'SIMPLE-202607-001', 19800.000, 'KRW', '약국',           '2028901234', 0, NULL, NULL, NULL, '01'),
    ('CARD_TEST_USER1_001', '202607', '20260716183000', 'SIMPLE-202607-002', 24000.000, 'KRW', '배달음식',       '2029012345', 0, NULL, NULL, NULL, '01'),
    ('CARD_TEST_USER1_001', '202607', '20260729140000', 'SIMPLE-202607-003', 20000.000, 'KRW', '생활용품점',     '2030123456', 0, NULL, NULL, NULL, '01');
