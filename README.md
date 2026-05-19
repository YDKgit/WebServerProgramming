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

CORS 안내
로컬 개발 환경에서의 원활한 연동을 위해 프론트엔드 기본 포트(예: localhost:3000, localhost:5173 등)에 대한 CORS 정책은 백엔드에서 모두 허용(Allow) 설정됨.


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

1. 인증 (Authentication)
로그인 (세션 기반)
 -Method: POST
 -URL: /api/auth/login
 -설명: 사용자 인증 및 세션 생성

Request Body (JSON)
JSON
{
  "loginId": "dev01",
  "password": "password123"
}

Response (JSON)
JSON
{
  "success": true,
  "data": {
    "id": 1,
    "name": "홍길동",
    "role": "DEVELOPER"
  }
}


2. 개발자 프로필 (Profile)
내 프로필 조회
 -Method: GET
 -URL: /api/member/profile

프로필 수정
 -Method: PUT
 -URL: /api/member/profile
 -주의: 기술 스택(검색 태그)이 5개를 초과(6개 이상)할 경우 400 에러 반환. 프론트엔드 단에서 사전 차단 필요.

프로필 이미지 업로드
 -Method: POST
 -URL: /api/member/profile/image
 -제약: multipart/form-data 형식으로 전송 필수. 서버는 저장된 파일의 URL 경로를 반환함.


3. 프로젝트 (Project)
프로젝트 목록 조회
 -Method: GET
 -URL: /api/projects
 -기능: 페이징, 정렬(최신순 등), 필터(웹/앱 등) 지원

프로젝트 등록
 -Method: POST
 -URL: /api/projects
 -설명: 의뢰인이 새로운 프로젝트를 등록

프로젝트 상세 조회
 -Method: GET
 -URL: /api/projects/{id}

지원자 목록 조회
 -Method: GET
 -URL: /api/projects/{id}/applicants
 -기능: 더보기 방식의 페이징 지원

내가 의뢰한 프로젝트 목록
 -Method: GET
 -URL: /api/projects/client/my

페이징 및 조건 검색 요청 예시
 -목록 조회: GET /api/projects?page=0&size=4&sort=latest&type=web
 -지원자 조회: GET /api/projects/{id}/applicants?page=0&size=2

페이징 응답 예시 (JSON)
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

프론트 참고: last: true 응답 시 화면의 '더보기' 버튼 비활성화 처리 필요.


4. 지원 (Application)
프로젝트 지원하기
 -Method: POST
 -URL: /api/applications

내가 지원한 프로젝트 목록
 -Method: GET
 -URL: /api/applications/my

지원서 상세 조회
 -Method: GET
 -URL: /api/applications/{applicationId}

지원 제한 사항
지원 내용(proposalContent)에 이메일, 전화번호 등 개인 연락처가 포함된 경우 서버에서 400 에러 반환 예정. (유효성 검사 로직 적용 후)


현재 더미 데이터 한계
 -고정 응답: 어떤 ID로 요청해도 항상 동일한 형태의 고정 더미 데이터가 반환됨.
 -세션 미적용: 로그인 성공 응답은 제공하나 실제 세션 유지는 되지 않음. (추후 적용 예정)
 -데이터 갱신 미반영: 등록/지원 성공 후에도 목록의 카운트(지원자 수 등)는 증가하지 않음. (DB 연동 후 반영 예정)


개발 단계
[x] 1단계 — Domain / DTO / Controller (더미 데이터 구조 설계 완료)

[ ] 2단계 — Repository / Service 비즈니스 로직 구현

[ ] 3단계 — DB 연동 (실제 가동 데이터 전환)

[ ] 4단계 — 세션 기반 인증 보안 적용 및 예외 처리
