-- Members: developers and clients
INSERT INTO member (login_id, password, name, role, profile_image, support_fields, is_available, is_onsite_available, region_main, region_sub, business_type, career_year, introduction, search_tags)
VALUES ('dev01', '1234', '김개발', 'DEVELOPER', null, '백엔드, 풀스택', true, false, '서울', '강남구', '개인프리랜서', '5년', '5년차 백엔드 개발자입니다. Spring Boot와 클라우드 환경에 강점이 있습니다.', 'Java,Spring,JPA,MySQL,Docker');

INSERT INTO member (login_id, password, name, role, profile_image, support_fields, is_available, is_onsite_available, region_main, region_sub, business_type, career_year, introduction, search_tags)
VALUES ('dev02', '1234', '이자바', 'DEVELOPER', null, '백엔드', true, true, '경기', '성남시', '개인프리랜서', '7년', '7년차 백엔드 개발자입니다. 대용량 트래픽 처리 경험이 있습니다.', 'Java,Spring,Kafka,Redis,AWS');

INSERT INTO member (login_id, password, name, role, profile_image, support_fields, is_available, is_onsite_available, region_main, region_sub, business_type, career_year, introduction, search_tags)
VALUES ('dev03', '1234', '최풀스택', 'DEVELOPER', null, '풀스택', true, true, '부산', '해운대구', '개인사업자', '3년', '풀스택 개발자입니다. React와 Spring Boot 모두 가능합니다.', 'React,TypeScript,Spring,MySQL,Docker');

INSERT INTO member (login_id, password, name, role, profile_image, support_fields, is_available, is_onsite_available, region_main, region_sub, business_type, career_year, introduction, search_tags)
VALUES ('client01', '1234', '박클라이언트', 'CLIENT', '/images/profile/client01.png', null, null, null, '서울', '서초구', '법인', null, '스타트업 CTO입니다.', null);

INSERT INTO member (login_id, password, name, role, profile_image, support_fields, is_available, is_onsite_available, region_main, region_sub, business_type, career_year, introduction, search_tags)
VALUES ('client02', '1234', '정의뢰인', 'CLIENT', '/images/profile/client02.png', null, null, null, '경기', '수원시', '개인사업자', null, '중소기업 대표입니다.', null);

-- Projects: project_fields is used by the frontend type filter (web/app)
INSERT INTO project (client_id, title, work_type, employment_type, project_fields, budget, status, deadline, description, required_skills, estimated_days, kickoff_date, created_at, planning_status, meeting_region, progress_method)
VALUES (4, '쇼핑몰 백엔드 API 개발', '원격', '도급', 'web', 300, 'RECRUITING', '2026-06-30', '상품, 주문, 결제 REST API 설계 및 개발. PG사 연동 포함.', 'Java 17, Spring Boot, JPA, MySQL', 90, '2026-07-10', '2026-05-01 10:00:00', '요구사항 정의 완료', '서울 / 온라인', '주 1회 온라인 미팅');

INSERT INTO project (client_id, title, work_type, employment_type, project_fields, budget, status, deadline, description, required_skills, estimated_days, kickoff_date, created_at, planning_status, meeting_region, progress_method)
VALUES (4, 'React 관리자 대시보드 UI 구축', '상주', '상주', 'web', 200, 'RECRUITING', '2026-06-15', '관리자 대시보드 화면 개발. 차트 및 테이블 컴포넌트 포함.', 'React, TypeScript, Tailwind CSS', 60, '2026-07-01', '2026-05-02 11:00:00', '화면 설계 완료', '경기 성남', '주 3일 상주');

INSERT INTO project (client_id, title, work_type, employment_type, project_fields, budget, status, deadline, description, required_skills, estimated_days, kickoff_date, created_at, planning_status, meeting_region, progress_method)
VALUES (5, '공공데이터 수집 배치 시스템 개발', '원격', '도급', 'web', 150, 'CLOSED', '2026-05-31', '공공 API 수집 및 정제 배치 시스템 개발.', 'Python, FastAPI, PostgreSQL', 30, '2026-06-05', '2026-04-20 09:00:00', '기획서 보유', '온라인', '비대면');

INSERT INTO project (client_id, title, work_type, employment_type, project_fields, budget, status, deadline, description, required_skills, estimated_days, kickoff_date, created_at, planning_status, meeting_region, progress_method)
VALUES (5, 'AWS 인프라 CI/CD 파이프라인 구축', '원격', '도급', 'web', 250, 'RECRUITING', '2026-07-10', 'GitHub Actions 기반 CI/CD 파이프라인 구축 및 AWS 배포 자동화.', 'AWS, Docker, GitHub Actions, Terraform', 60, '2026-07-15', '2026-05-03 14:00:00', '기능 목록 보유', '온라인', '비대면');

INSERT INTO project (client_id, title, work_type, employment_type, project_fields, budget, status, deadline, description, required_skills, estimated_days, kickoff_date, created_at, planning_status, meeting_region, progress_method)
VALUES (4, '사내 인사 관리 시스템 리뉴얼', '상주', '상주', 'web', 500, 'RECRUITING', '2026-08-31', '기존 인사 시스템을 Vue.js와 Node.js로 리뉴얼합니다.', 'Vue.js, Node.js, Oracle', 180, '2026-09-01', '2026-05-05 10:00:00', 'DB 설계 필요', '서울', '주 2일 상주');

INSERT INTO project (client_id, title, work_type, employment_type, project_fields, budget, status, deadline, description, required_skills, estimated_days, kickoff_date, created_at, planning_status, meeting_region, progress_method)
VALUES (5, '모바일 앱 백엔드 API 구축', '원격', '도급', 'app', 350, 'RECRUITING', '2026-07-20', 'iOS/Android 공용 REST API 서버 개발.', 'Java, Spring Boot, FCM, MySQL', 90, '2026-08-01', '2026-05-06 09:00:00', '요구사항 정리 중', '대구 / 온라인', '주 1회 미팅');

INSERT INTO project (client_id, title, work_type, employment_type, project_fields, budget, status, deadline, description, required_skills, estimated_days, kickoff_date, created_at, planning_status, meeting_region, progress_method)
VALUES (4, '실시간 채팅 시스템 개발', '원격', '도급', 'web', 200, 'RECRUITING', '2026-07-05', 'WebSocket 기반 실시간 채팅 기능 개발.', 'Spring WebSocket, Redis, MySQL', 45, '2026-07-10', '2026-05-07 13:00:00', 'API 일부 완성', '온라인', '비대면');

INSERT INTO project (client_id, title, work_type, employment_type, project_fields, budget, status, deadline, description, required_skills, estimated_days, kickoff_date, created_at, planning_status, meeting_region, progress_method)
VALUES (5, '데이터 시각화 대시보드', '상주', '상주', 'web', 180, 'CLOSED', '2026-05-20', 'D3.js 기반 데이터 시각화 대시보드 구축.', 'React, D3.js, TypeScript', 45, '2026-05-25', '2026-04-15 10:00:00', '화면 설계 완료', '부산', '상주');

INSERT INTO project (client_id, title, work_type, employment_type, project_fields, budget, status, deadline, description, required_skills, estimated_days, kickoff_date, created_at, planning_status, meeting_region, progress_method)
VALUES (4, '전자상거래 결제 모듈 개발', '원격', '도급', 'web', 280, 'RECRUITING', '2026-08-10', '다양한 PG사 연동 결제 모듈 개발 및 정산 API 구현.', 'Java, Spring Boot, TossPayments API', 75, '2026-08-15', '2026-05-08 11:00:00', '기획서 보유', '온라인', '비대면');

INSERT INTO project (client_id, title, work_type, employment_type, project_fields, budget, status, deadline, description, required_skills, estimated_days, kickoff_date, created_at, planning_status, meeting_region, progress_method)
VALUES (5, 'ERP 시스템 커스터마이징', '상주', '상주', 'web', 600, 'RECRUITING', '2026-09-30', '기존 ERP 시스템에 맞춤 모듈을 추가 개발합니다.', 'Java, JSP, Oracle, jQuery', 120, '2026-10-01', '2026-05-10 09:00:00', '요구사항 정의 완료', '경기 수원', '상주');

UPDATE project SET participation_fields = '기획,개발' WHERE id IN (1, 4, 9);
UPDATE project SET participation_fields = '디자인,개발' WHERE id IN (2, 8);
UPDATE project SET participation_fields = '개발' WHERE id IN (3, 6, 7, 10);
UPDATE project SET participation_fields = '기획,디자인,개발' WHERE id = 5;

-- Applications
INSERT INTO application (project_id, developer_id, work_duration, bid_amount, proposal_content, tech_category, experience_level, headcount, applied_at, status)
VALUES (1, 1, 60, 250, '쇼핑몰 유사 프로젝트 3건 완료 경험이 있어 빠른 납기가 가능합니다.', '백엔드', '중급', 1, '2026-05-10 14:30:00', 'PENDING');

INSERT INTO application (project_id, developer_id, work_duration, bid_amount, proposal_content, tech_category, experience_level, headcount, applied_at, status)
VALUES (1, 2, 90, 350, 'Spring Boot 기반 API 개발 경험을 다수 보유하고 있으며 즉시 투입 가능합니다.', '백엔드', '고급', 1, '2026-05-12 09:00:00', 'PENDING');

INSERT INTO application (project_id, developer_id, work_duration, bid_amount, proposal_content, tech_category, experience_level, headcount, applied_at, status)
VALUES (2, 1, 45, 200, 'React 프로젝트 경험이 많아 빠르게 UI를 구현할 수 있습니다.', '프론트엔드', '중급', 1, '2026-05-13 10:00:00', 'PENDING');

INSERT INTO application (project_id, developer_id, work_duration, bid_amount, proposal_content, tech_category, experience_level, headcount, applied_at, status)
VALUES (3, 3, 30, 180, '배치 시스템 개발 경험이 있습니다. 일정 준수에 자신 있습니다.', '백엔드', '초급', 1, '2026-04-28 16:45:00', 'PENDING');

INSERT INTO application (project_id, developer_id, work_duration, bid_amount, proposal_content, tech_category, experience_level, headcount, applied_at, status)
VALUES (4, 2, 60, 300, 'AWS 인프라 구축 및 CI/CD 파이프라인 경험이 있습니다.', 'DevOps', '고급', 1, '2026-05-05 10:00:00', 'PENDING');
