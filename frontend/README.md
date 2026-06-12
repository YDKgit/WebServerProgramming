# 프리모아 클론코딩 프론트엔드

React + Vite SPA 프로젝트입니다. PPT 요구사항 기준으로 개발자/의뢰인 화면 흐름을 구성했습니다.

## 실행

```bash
npm install
npm run dev
```

## API 주소 설정

`.env.example` 파일을 `.env`로 복사한 뒤 백엔드 주소를 맞춰주세요.

```env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_USE_MOCK=false
```

백엔드가 아직 없으면 임시로 `VITE_USE_MOCK=true`로 화면만 확인할 수 있습니다. 단, 검사 시에는 서버에서 정렬/페이징 데이터를 받아야 하므로 mock 사용을 끄는 것을 권장합니다.

## 주요 라우트

- `/projects`: 랜딩/프로젝트 찾기
- `/projects/:projectId`: 프로젝트 상세 요약
- `/projects/:projectId/apply`: 프로젝트 지원
- `/developer/mypage`: 개발자 마이페이지
- `/client/new-project`: 의뢰인 프로젝트 의뢰하기
- `/client/mypage`: 의뢰인 프로젝트 관리

## 백엔드 API 예상 형태

프론트에서 호출하는 API는 `src/api/api.js`에 모아두었습니다. 실제 백엔드 URL이 다르면 이 파일만 수정하면 됩니다.
