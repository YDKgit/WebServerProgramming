# [프로젝트명] API 서버

Spring Boot 기반 REST API 서버
프론트엔드 개발자가 즉시 작업을 시작할 수 있도록 Swagger + 더미 데이터로 구성

---

## 기술 스택

- Java 17
- Spring Boot 3.x
- Spring Data JPA
- SpringDoc OpenAPI (Swagger UI)
- H2 (인메모리 DB, 개발용)
- Lombok

---

## 실행 방법

```bash
./gradlew bootRun

실행 후 아래 주소로 Swagger UI 접속:
http://localhost:8080/swagger-ui/index.html

CORS 안내: 로컬 개발 환경에서의 원활한 연동을 위해 프론트엔드 기본 포트(예: localhost:3000, localhost:5173 등)에 대한 CORS 정책은 백엔드에서 모두 허용(Allow) 설정됨.

패키지 구조
Plaintext
src/main/java/com/example/springstudy/
│
├── SpringstudyApplication.java
│
├── controller/
│   ├── AuthController.java
│   ├── MemberController.java
│   ├── ProjectController.java
│   └── ApplicationController.java
│
├── dto/
│   ├── AuthDto.java
│   ├── MemberDto.java
│   ├── ProjectDto.java
│   ├── ApplicationDto.java
│   └── CommonResponse.java
│
├── domain/
│   ├── Member.java
│   ├── Project.java
│   ├── Application.java
│   ├── Role.java
│   └── ProjectStatus.java
│
├── repository/            ← 추후 추가 예정
└── service/               ← 추후 추가 예정

API 목록

공통 응답 구조
모든 API는 아래 형태로 응답
JSON
{
  "success": true,
  "data": { ... }
}

인증

Request Body
JSON
{
  "loginId": "dev01",
  "password": "password123"
}

Response
JSON
{
  "success": true,
  "data": {
    "id": 1,
    "name": "홍길동",
    "role": "DEVELOPER"
  }
}

개발자 프로필

이미지 업로드 제약: 이미지 업로드는 multipart/form-data 형식으로 전송해야 함. (JSON 아님) 서버는 이미지를 저장한 후 저장 경로(URL)를 반환하므로, 클라이언트는 이를 받아 화면의 이미지를 갱신.

태그 개수 제한 경고: 프로필 수정(PUT) 시 기술 스택(검색 태그)이 **5개를 초과(6개 이상)**할 경우 서버에서 400 Bad Request 에러를 반환할 예정. 사용자가 태그를 6개 이상 등록하려고 하면 프론트엔드 단에서 알림창(alert) 등으로 사전에 차단 필요. (태그 옆 X 버튼을 통한 삭제 기능 구현 필요)

프로젝트

페이징 및 조건 검색 요청 예시
Plaintext
GET /api/projects?page=0&size=4&sort=latest&type=web
GET /api/projects/{id}/applicants?page=0&size=2
sort: 정렬 기준 (예: latest 최신순 등)
type: 프로젝트 형태 필터링 (예: web, app 등)


페이징 응답 구조
JSON
{
  "success": true,
  "data": {
    "content": [...],
    "pageNumber": 0,
    "pageSize": 4,
    "totalElements": 12,
    "totalPages": 3,
    "last": false
  }
}

더보기 버튼 처리: 응답 데이터의 last: true 이면 마지막 페이지이므로 화면의 '더보기' 버튼을 비활성화 필요. 새로운 페이지를 요청할 때마다 서버로부터 누적이 아닌 해당 페이지의 데이터만 수신.


지원
지원서 제한 사항: 지원서 내용(proposalContent)에 이메일이나 전화번호 등 개인 연락처가 포함된 경우 서버에서 400 Bad Request 에러를 반환할 예정. (DB 연동 및 유효성 검사 로직 도입 후 적용 예정)

현재 더미 데이터 한계
개발 단계
[x] 1단계 — Domain / DTO / Controller (더미 데이터 구조 설계 완료)

[ ] 2단계 — Repository / Service 비즈니스 로직 구현

[ ] 3단계 — DB 연동 (더미 데이터 → 실제 가동 데이터 전환)

[ ] 4단계 — 세션 기반 인증 보안 적용 및 상세 예외 처리
