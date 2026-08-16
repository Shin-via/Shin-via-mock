# Shinvia Mock 카드 MyData API

모든 카드 API는 `Authorization: Bearer {access_token}`으로 사용자를 식별한다. access token에서 Redis의 `mydata:at:ci:{access_token}`를 조회해 CI를 얻고, 해당 CI의 데이터만 반환한다.

| 구분 | Method / URI | 설명 |
| --- | --- | --- |
| 카드-001 | `GET /v2/card/cards` | 보유 신용카드 목록 |
| 카드-002 | `GET /v2/card/cards/{card_id}` | 카드 기본정보 |
| 카드-004 | `GET /v2/card/bills` | 고객 단위 월별 청구 합산 |
| 카드-005 | `GET /v2/card/bills/detail` | 월별 청구 상세 거래내역 |

## 공통

- 신용카드만 응답한다. `card_type`은 항상 `"01"`, `card_member`는 항상 `"1"`이다.
- `limit`은 1~500만 허용한다.
- `limit`을 초과한 데이터가 있으면 `next_page`에 `"1"`을 반환한다.
- 카드 ID를 직접 받는 카드-002도 토큰의 CI 소유 카드인지 검증한다.

## 카드-001

`GET /v2/card/cards?org_code=004&search_timestamp=20260810111500&limit=10`

응답의 `card_list[].card_id`를 카드-002 호출에 사용한다.

## 카드-002

`GET /v2/card/cards/{card_id}?org_code=004&search_timestamp=20260810111500`

존재하지 않거나 다른 CI의 카드 ID는 `404 Not Found`를 반환한다.

## 카드-004

`GET /v2/card/bills?org_code=004&from_month=202605&to_month=202607&limit=10`

`mock_card_bill`의 카드별 행을 `ci + charge_month + settlement_seq_no` 기준으로 묶어 `charge_amt`를 합산한다. `seqno`, `charge_month`는 카드-005 호출에 사용한다.

## 카드-005

`GET /v2/card/bills/detail?org_code=004&seqno=0&charge_month=202607&limit=10`

상세 테이블에는 결제순번 컬럼이 없으므로 현재 `seqno`는 호환을 위해 수신만 하고, `charge_month`와 토큰 CI 기준으로 상세를 조회한다. 결제순번별 상세 분리가 필요해지면 `mock_card_bill_detail`에 `settlement_seq_no`를 추가한 뒤 해당 조건을 조회에 포함해야 한다.
