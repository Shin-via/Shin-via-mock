# Mock OAuth · 카드 API 실패 케이스 안내

`mock-failure.postman_collection.json`은 Shinvia Mock 서버가 잘못된 요청을 정상적으로 거절하는지 확인하는 Postman 컬렉션이다.

## 실행 전 준비

- Mock 서버: `http://localhost:9090`
- Redis: `localhost:6379`
- MySQL: `localhost:3307`, database `mock_openbanking`
- OAuth 테스트 클라이언트
  - `org_code`: `004`
  - `client_id`: `mock-card-client`
  - `client_secret`: `mock-card-secret`
  - `redirect_uri`: `http://localhost:3000/callback`
- 카드 보유 사용자 CI: `TEST_CI_1000000001`

Postman에서 `mock-failure.postman_collection.json`을 Import한다. 컬렉션 변수의 `base_url`은 기본값 `http://localhost:9090`을 사용한다.

각 요청의 **Tests** 탭에는 기대 HTTP 상태 코드를 검증하는 스크립트가 들어 있다. 서버의 오류 응답 body 형식은 예외 종류에 따라 달라질 수 있으므로, 이 컬렉션은 우선 상태 코드로 실패 여부를 판단한다.

## 토큰과 CI 식별 방식

정상적인 카드 API 호출은 `Authorization: Bearer {access_token}` 헤더를 사용한다. 서버는 access token으로 Redis에서 CI를 찾고, 그 CI로 카드 데이터를 조회한다.

```text
Authorization: Bearer mock_at_TEST_CI_1000000001_<uuid>
        │
        ▼
Redis: mydata:at:ci:{access_token}
        │
        ▼
TEST_CI_1000000001
        │
        ▼
mock_card_info.ci = TEST_CI_1000000001
```

따라서 카드 목록 API에 CI를 직접 전달하지 않아도 된다. 존재하지 않거나 만료된 토큰은 CI로 변환할 수 없으므로 `401 Unauthorized`가 되어야 한다.

## OAuth 실패 케이스

### 1. 인가 코드: `x-user-ci` 헤더 누락

```http
GET /v2/oauth/2.0/authorize?org_code=004&response_type=code&client_id=mock-card-client&redirect_uri=http://localhost:3000/callback
x-api-tran-id: FAIL-AUTH-001
```

인가 코드는 특정 사용자의 CI에 연결되어 발급된다. `x-user-ci`가 없으면 어떤 사용자에 대한 연결인지 알 수 없어서 `400 Bad Request`가 반환된다.

### 2. 인가 코드: 등록되지 않은 `client_id`

```http
GET /v2/oauth/2.0/authorize?...&client_id=unknown-client
x-user-ci: TEST_CI_1000000001
```

`mock_clients`에 없는 클라이언트이므로 `400 Bad Request`가 반환된다. 정상 테스트용 `client_id`는 `mock-card-client`이다.

### 3. 토큰 발급: `grant_type` 누락

```http
POST /v2/oauth/2.0/token
Content-Type: application/x-www-form-urlencoded

client_id=mock-card-client
```

토큰 발급인지 갱신인지 구분할 수 없으므로 `400 Bad Request`가 반환된다. 허용되는 값은 `authorization_code` 또는 `refresh_token`이다.

### 4. 토큰 발급: 지원하지 않는 `grant_type`

```http
POST /v2/oauth/2.0/token

grant_type=password
```

Mock 서버는 password grant를 구현하지 않았으므로 `400 Bad Request`가 반환된다.

### 5. 토큰 발급: 존재하지 않는 인가 코드

```http
POST /v2/oauth/2.0/token

grant_type=authorization_code
client_id=mock-card-client
org_code=004
redirect_uri=http://localhost:3000/callback
code=MOCK_CODE_DOES_NOT_EXIST
```

`mock_authorizations`에서 인가 코드를 찾을 수 없으므로 `400 Bad Request`가 반환된다. 이미 사용한 인가 코드 또는 만료된 인가 코드도 같은 방식으로 거절된다.

### 6. 토큰 갱신: 잘못된 refresh token

```http
POST /v2/oauth/2.0/token

grant_type=refresh_token
client_id=mock-card-client
client_secret=mock-card-secret
org_code=004
is_refreshed=N
refresh_token=mock_rt_invalid_token
```

Redis에 `mydata:rt:ci:mock_rt_invalid_token` 키가 없으므로 사용자를 찾을 수 없다. 이 경우 `400 Bad Request`가 반환된다.

## 카드 API 실패 케이스

현재 카드 목록 endpoint는 아래와 같다.

```http
GET /v2.0/cards
```

필수 요청값은 다음과 같다.

| 구분 | 필수값 |
| --- | --- |
| Header | `Authorization`, `x-api-tran-id`, `x-api-type` |
| Query | `org_code`, `search_timestamp`, `limit` |

### 7. 카드 목록: Authorization 헤더 누락

```http
GET /v2.0/cards?org_code=004&search_timestamp=0&limit=10
x-api-tran-id: FAIL-CARD-001
x-api-type: CARD
```

컨트롤러의 `Authorization` 헤더는 필수 선언이다. Spring이 컨트롤러 실행 전에 요청을 거절하므로 현재 구현에서는 `400 Bad Request`가 반환된다.

> API 정책상 인증 실패를 일관되게 `401 Unauthorized`로 반환하고 싶다면 `@RequestHeader(value = "Authorization", required = false)`로 바꾸고, `resolveCi`의 헤더 누락 검증에 맡기면 된다.

### 8. 카드 목록: 유효하지 않은 access token

```http
GET /v2.0/cards?org_code=004&search_timestamp=0&limit=10
Authorization: Bearer mock_at_invalid_token
x-api-tran-id: FAIL-CARD-002
x-api-type: CARD
```

Redis의 `mydata:at:ci:mock_at_invalid_token` 키를 찾을 수 없으므로 CI를 확정할 수 없다. 이 경우 컨트롤러가 `401 Unauthorized`를 반환한다.

### 9. 카드 목록: `limit` 누락

```http
GET /v2.0/cards?org_code=004&search_timestamp=0
Authorization: Bearer mock_at_invalid_token
x-api-tran-id: FAIL-CARD-003
x-api-type: CARD
```

`limit`은 필수 query parameter다. 파라미터 바인딩 단계에서 요청이 거절되어 `400 Bad Request`가 반환된다. 이 검증은 access token 검증보다 먼저 발생할 수 있다.

## 컬렉션 실행 결과 해석

- 각 요청이 Tests에서 `PASS`이면 의도한 실패가 발생한 것이다.
- `200 OK`가 나오면 입력 검증 또는 인증 검증이 빠졌을 가능성이 있다.
- 서버가 내려가 있으면 HTTP 상태 코드 대신 연결 실패가 표시된다. 먼저 Mock 서버와 Redis 실행 상태를 확인한다.
- DB 데이터는 OAuth 클라이언트 검증에 사용된다. `mock_clients`의 `mock-card-client` 행이 없으면 일부 테스트가 예상과 다르게 실패할 수 있다.
