-- Member (개발자 3명, 의뢰인 2명)
INSERT INTO member (login_id, password, name, role, profile_image, support_fields, is_available, is_onsite_available, region_main, region_sub, business_type, career_year, introduction, search_tags)
VALUES ('dev01', '1234', '김개발', 'DEVELOPER', '/images/profile/dev01.png', '백엔드, 풀스택', true, false, '서울', '강남구', '프리랜서', '5년', '5년차 백엔드 개발자입니다. Spring Boot와 클라우드 환경에 강점이 있습니다.', 'Java,Spring,JPA,MySQL,Docker');
INSERT INTO member (login_id, password, name, role, profile_image, support_fields, is_available, is_onsite_available, region_main, region_sub, business_type, career_year, introduction, search_tags)
VALUES ('dev02', '1234', '이자바', 'DEVELOPER', '/images/profile/dev02.png', '백엔드', true, true, '경기', '성남시', '프리랜서', '7년', '7년차 백엔드 개발자입니다. 대용량 트래픽 처리 경험이 있습니다.', 'Java,Spring,Kafka,Redis,AWS');
INSERT INTO member (login_id, password, name, role, profile_image, support_fields, is_available, is_onsite_available, region_main, region_sub, business_type, career_year, introduction, search_tags)
VALUES ('dev03', '1234', '최스프링', 'DEVELOPER', '/images/profile/dev03.png', '풀스택', true, true, '부산', '해운대구', '사업자', '3년', '풀스택 개발자입니다. React와 Spring Boot 모두 가능합니다.', 'React,TypeScript,Spring,MySQL,Docker');
INSERT INTO member (login_id, password, name, role, profile_image, support_fields, is_available, is_onsite_available, region_main, region_sub, business_type, career_year, introduction, search_tags)
VALUES ('client01', '1234', '박클라이언트', 'CLIENT', '/images/profile/client01.png', null, null, null, '서울', '서초구', '법인', null, '스타트업 CTO입니다.', null);
INSERT INTO member (login_id, password, name, role, profile_image, support_fields, is_available, is_onsite_available, region_main, region_sub, business_type, career_year, introduction, search_tags)
VALUES ('client02', '1234', '정의뢰인', 'CLIENT', '/images/profile/client02.png', null, null, null, '경기', '수원시', '개인사업자', null, '중소기업 대표입니다.', null);

-- Project (client_id 4 = 박클라이언트, 5 = 정의뢰인)
INSERT INTO project (client_id, title, work_type, employment_type, project_fields, budget, status, deadline, description, required_skills, estimated_days, kickoff_date, created_at)
VALUES (4, '쇼핑몰 백엔드 API 개발', '원격', '도급', '백엔드', 300, 'RECRUITING', '2026-06-30', '상품/주문/결제 REST API 설계 및 개발. PG사 연동 포함.', 'Java 17, Spring Boot, JPA, MySQL', 90, '2026-07-10', '2026-05-01 10:00:00');
INSERT INTO project (client_id, title, work_type, employment_type, project_fields, budget, status, deadline, description, required_skills, estimated_days, kickoff_date, created_at)
VALUES (4, 'React 관리자 대시보드 UI 구축', '상주', '상주', '프론트엔드', 200, 'RECRUITING', '2026-06-15', '관리자 대시보드 화면 개발. 차트 및 테이블 컴포넌트 포함.', 'React, TypeScript, Tailwind CSS', 60, '2026-07-01', '2026-05-02 11:00:00');
INSERT INTO project (client_id, title, work_type, employment_type, project_fields, budget, status, deadline, description, required_skills, estimated_days, kickoff_date, created_at)
VALUES (5, '공공데이터 수집 배치 시스템 개발', '원격', '도급', '백엔드', 150, 'CLOSED', '2026-05-31', '공공 API 수집 및 정제 배치 시스템 개발.', 'Python, FastAPI, PostgreSQL', 30, '2026-06-05', '2026-04-20 09:00:00');
INSERT INTO project (client_id, title, work_type, employment_type, project_fields, budget, status, deadline, description, required_skills, estimated_days, kickoff_date, created_at)
VALUES (5, 'AWS 인프라 CI/CD 파이프라인 구축', '원격', '도급', 'DevOps', 250, 'RECRUITING', '2026-07-10', 'GitHub Actions 기반 CI/CD 파이프라인 구축 및 AWS 배포 자동화.', 'AWS, Docker, GitHub Actions, Terraform', 60, '2026-07-15', '2026-05-03 14:00:00');
INSERT INTO project (client_id, title, work_type, employment_type, project_fields, budget, status, deadline, description, required_skills, estimated_days, kickoff_date, created_at)
VALUES (4, '사내 인사 관리 시스템 리뉴얼', '상주', '상주', '풀스택', 500, 'RECRUITING', '2026-08-31', '레거시 인사 시스템을 Vue.js + Node.js로 리뉴얼.', 'Vue.js, Node.js, Oracle', 180, '2026-09-01', '2026-05-05 10:00:00');
INSERT INTO project (client_id, title, work_type, employment_type, project_fields, budget, status, deadline, description, required_skills, estimated_days, kickoff_date, created_at)
VALUES (5, '모바일 앱 백엔드 API 구축', '원격', '도급', '백엔드', 350, 'RECRUITING', '2026-07-20', 'iOS/Android 공용 REST API 서버 개발.', 'Java, Spring Boot, FCM, MySQL', 90, '2026-08-01', '2026-05-06 09:00:00');
INSERT INTO project (client_id, title, work_type, employment_type, project_fields, budget, status, deadline, description, required_skills, estimated_days, kickoff_date, created_at)
VALUES (4, '실시간 채팅 시스템 개발', '원격', '도급', '백엔드', 200, 'RECRUITING', '2026-07-05', 'WebSocket 기반 실시간 채팅 기능 개발.', 'Spring WebSocket, Redis, MySQL', 45, '2026-07-10', '2026-05-07 13:00:00');
INSERT INTO project (client_id, title, work_type, employment_type, project_fields, budget, status, deadline, description, required_skills, estimated_days, kickoff_date, created_at)
VALUES (5, '데이터 시각화 대시보드', '상주', '상주', '프론트엔드', 180, 'CLOSED', '2026-05-20', 'D3.js 기반 데이터 시각화 대시보드 구축.', 'React, D3.js, TypeScript', 45, '2026-05-25', '2026-04-15 10:00:00');
INSERT INTO project (client_id, title, work_type, employment_type, project_fields, budget, status, deadline, description, required_skills, estimated_days, kickoff_date, created_at)
VALUES (4, '전자상거래 결제 모듈 개발', '원격', '도급', '백엔드', 280, 'RECRUITING', '2026-08-10', '다양한 PG사 연동 결제 모듈 개발 및 정산 API 구현.', 'Java, Spring Boot, TossPayments API', 75, '2026-08-15', '2026-05-08 11:00:00');
INSERT INTO project (client_id, title, work_type, employment_type, project_fields, budget, status, deadline, description, required_skills, estimated_days, kickoff_date, created_at)
VALUES (5, 'ERP 시스템 커스터마이징', '상주', '상주', '풀스택', 600, 'RECRUITING', '2026-09-30', '기존 ERP 시스템에 맞춤 모듈 추가 개발.', 'Java, JSP, Oracle, jQuery', 120, '2026-10-01', '2026-05-10 09:00:00');

-- application
INSERT INTO application (project_id, developer_id, work_duration, bid_amount, proposal_content, tech_category, experience_level, headcount, applied_at)
VALUES (1, 1, 60, 250, '쇼핑몰 유사 프로젝트 3건 완료, 빠른 납기 가능합니다.', '백엔드', '중급', 1, '2026-05-10 14:30:00');
INSERT INTO application (project_id, developer_id, work_duration, bid_amount, proposal_content, tech_category, experience_level, headcount, applied_at)
VALUES (1, 2, 90, 350, 'Spring Boot 기반 API 개발 경험 다수 보유, 즉시 투입 가능합니다.', '백엔드', '고급', 1, '2026-05-12 09:00:00');
INSERT INTO application (project_id, developer_id, work_duration, bid_amount, proposal_content, tech_category, experience_level, headcount, applied_at)
VALUES (2, 1, 45, 200, 'React 프로젝트 다수 경험, 빠른 UI 구현 자신합니다.', '프론트엔드', '중급', 1, '2026-05-13 10:00:00');
INSERT INTO application (project_id, developer_id, work_duration, bid_amount, proposal_content, tech_category, experience_level, headcount, applied_at)
VALUES (3, 3, 30, 180, '배치 시스템 개발 경험 있습니다. 납기 준수 자신합니다.', '백엔드', '초급', 1, '2026-04-28 16:45:00');
INSERT INTO application (project_id, developer_id, work_duration, bid_amount, proposal_content, tech_category, experience_level, headcount, applied_at)
VALUES (4, 2, 60, 300, 'AWS 인프라 구축 및 CI/CD 파이프라인 경험 있습니다.', 'DevOps', '고급', 1, '2026-05-05 10:00:00');
