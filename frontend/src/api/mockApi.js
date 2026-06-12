import { addDays, calcDday } from '../utils/format';

const projects = [
  {
    id: 1,
    title: 'AI 기반 쇼핑몰 플랫폼 개발',
    category: 'web',
    type: 'OUTSOURCING',
    status: 'RECRUITING',
    budget: 8000000,
    expectedPeriod: '90일',
    applicantCount: 12,
    deadline: addDays(8),
    skills: ['React', 'Spring Boot', 'JPA'],
    fields: ['개발', '기획'],
    planningStatus: '기획서 보유',
    meetingRegion: '서울',
    workMethod: '온라인 협업',
    description: 'AI 추천 기능을 포함한 쇼핑몰 웹 플랫폼을 개발합니다.',
  },
  {
    id: 2,
    title: 'React 관리자 페이지 리뉴얼',
    category: 'web',
    type: 'RESIDENT',
    status: 'RECRUITING',
    budget: 7000000,
    expectedPeriod: '3개월',
    applicantCount: 5,
    deadline: addDays(12),
    skills: ['React', 'TypeScript', 'REST API'],
    fields: ['개발'],
    planningStatus: '화면 설계 완료',
    meetingRegion: '경기 구리',
    workMethod: '기간제 상주',
    description: '기존 관리자 페이지의 UI와 API 연동 구조를 개선합니다.',
  },
  {
    id: 3,
    title: '동네 예약 서비스 앱 개발',
    category: 'app',
    type: 'OUTSOURCING',
    status: 'RECRUITING',
    budget: 15000000,
    expectedPeriod: '120일',
    applicantCount: 18,
    deadline: addDays(4),
    skills: ['Flutter', 'Firebase', 'Spring'],
    fields: ['개발', '디자인'],
    planningStatus: '요구사항 정리 중',
    meetingRegion: '대구',
    workMethod: '주 1회 미팅',
    description: '동네 검색, 예약, 리뷰 기능을 포함한 모바일 앱 개발입니다.',
  },
  {
    id: 4,
    title: '공공기관 민원 챗봇 구축',
    category: 'web',
    type: 'OUTSOURCING',
    status: 'RECRUITING',
    budget: 10000000,
    expectedPeriod: '80일',
    applicantCount: 7,
    deadline: addDays(20),
    skills: ['Vue', 'Node.js', 'LLM'],
    fields: ['개발', '기획'],
    planningStatus: '기능 목록 보유',
    meetingRegion: '온라인',
    workMethod: '비대면',
    description: '민원 FAQ 기반 챗봇과 관리자 답변 관리 화면을 만듭니다.',
  },
  {
    id: 5,
    title: '병원 예약 웹 시스템 개발',
    category: 'web',
    type: 'RESIDENT',
    status: 'RECRUITING',
    budget: 6500000,
    expectedPeriod: '2개월',
    applicantCount: 4,
    deadline: addDays(16),
    skills: ['Java', 'Spring Boot', 'MySQL'],
    fields: ['개발'],
    planningStatus: 'DB 설계 필요',
    meetingRegion: '부산',
    workMethod: '주 3일 상주',
    description: '진료 예약, 의사 일정, 환자 관리 기능을 개발합니다.',
  },
  {
    id: 6,
    title: 'ESG 리스크 대시보드 프론트 개발',
    category: 'web',
    type: 'OUTSOURCING',
    status: 'CLOSED',
    budget: 5000000,
    expectedPeriod: '45일',
    applicantCount: 10,
    deadline: addDays(-1),
    skills: ['React', 'Chart.js', 'FastAPI'],
    fields: ['개발'],
    planningStatus: 'API 일부 완성',
    meetingRegion: '온라인',
    workMethod: '비대면',
    description: '뉴스 기반 ESG 리스크 데이터를 시각화하는 대시보드입니다.',
  },
];

projects.forEach((project) => {
  project.clientId = 2;
});

let profile = {
  name: '김개발',
  profileImage: '',
  supportFields: '개발,기획',
  isAvailable: true,
  isOnsiteAvailable: false,
  regionMain: '서울',
  regionSub: '강남구',
  businessType: '개인프리랜서',
  careerYear: '5년',
  introduction: 'React와 Spring API 연동 프로젝트 경험이 있습니다.',
  searchTags: 'React,Spring,JPA',
};

let currentMockUser = null;

let applications = [
  {
    id: 101,
    projectId: 1,
    projectTitle: projects[0].title,
    projectSummary: projects[0].description,
    type: 'OUTSOURCING',
    bidAmount: 7800000,
    workPeriod: '80일',
    applicantCount: 12,
    appliedAt: '2026-06-01',
    status: 'PENDING',
    content: '쇼핑몰과 추천 시스템 연동 경험을 바탕으로 안정적으로 구현하겠습니다.',
  },
  {
    id: 102,
    projectId: 2,
    projectTitle: projects[1].title,
    projectSummary: projects[1].description,
    type: 'RESIDENT',
    bidAmount: 7000000,
    workPeriod: '3개월',
    applicantCount: 5,
    appliedAt: '2026-06-02',
    status: 'PENDING',
    content: '관리자 페이지 리뉴얼과 REST API 연동 경험이 있습니다.',
  },
];

let clientProjects = projects.slice(0, 3).map((project, index) => ({
  ...project,
  applications: Array.from({ length: index === 0 ? 5 : 3 }, (_, i) => ({
    id: Number(`${project.id}${i + 1}`),
    applicationId: Number(`${project.id}${i + 1}`),
    projectId: project.id,
    projectTitle: project.title,
    developerName: `지원자 ${i + 1}`,
    techCategory: project.fields?.[0] || '개발',
    experienceLevel: i % 2 === 0 ? '중급' : '고급',
    workDuration: 60 + i * 10,
    headcount: 1,
    order: i + 1,
    bidAmount: project.type === 'RESIDENT' ? project.budget : project.budget - i * 100000,
    type: project.type,
    appliedAt: `2026-06-${String(i + 1).padStart(2, '0')}`,
    proposalSummary: `${project.title}에 지원합니다.`,
    proposalContent: `${project.title}에 지원합니다. 요구사항을 분석해 일정 안에 구현하겠습니다.`,
    status: 'PENDING',
  })),
}));

function ok(data) {
  return new Promise((resolve) => setTimeout(() => resolve(data), 150));
}

function sortProjects(list, sort) {
  const copied = [...list];
  switch (sort) {
    case 'deadline':
      return copied.sort((a, b) => new Date(a.deadline) - new Date(b.deadline));
    case 'budgetDesc':
      return copied.sort((a, b) => b.budget - a.budget);
    case 'applicantsDesc':
      return copied.sort((a, b) => b.applicantCount - a.applicantCount);
    case 'latest':
    default:
      return copied.sort((a, b) => b.id - a.id);
  }
}

function parseBody(options) {
  return JSON.parse(options.body || '{}');
}

function pageResponse(items, page, size) {
  const start = page * size;
  const content = items.slice(start, start + size);
  const totalPages = Math.ceil(items.length / size);
  return {
    content,
    page,
    size,
    totalElements: items.length,
    totalPages,
    last: page >= totalPages - 1,
  };
}

export async function mockApi(path, options = {}) {
  const method = options.method || 'GET';
  const url = new URL(path, 'http://mock.local');

  if (url.pathname === '/auth/login' && method === 'POST') {
    const payload = parseBody(options);
    currentMockUser = {
      id: payload.loginId === 'client01' ? 2 : 1,
      name: payload.loginId === 'client01' ? '의뢰인' : '개발자',
      role: payload.loginId === 'client01' ? 'CLIENT' : 'DEVELOPER',
    };
    return ok(currentMockUser);
  }

  if (url.pathname === '/projects' && method === 'GET') {
    const type = url.searchParams.get('type') || 'web';
    const sort = url.searchParams.get('sort') || 'latest';
    const status = url.searchParams.get('status') || 'ALL';
    const page = Number(url.searchParams.get('page') || 0);
    const size = Number(url.searchParams.get('size') || 4);
    const typeFiltered = type === 'ALL' ? projects : projects.filter((project) => project.category === type);
    const filtered = status === 'ALL'
      ? typeFiltered
      : typeFiltered.filter((project) => project.status === status);
    return ok(pageResponse(sortProjects(filtered, sort), page, size));
  }

  if (url.pathname === '/projects' && method === 'POST') {
    const payload = parseBody(options);
    const saved = {
      id: Date.now(),
      category: 'web',
      status: 'RECRUITING',
      applicantCount: 0,
      clientId: currentMockUser?.id,
      expectedPeriod: payload.expectedPeriod || '협의',
      applications: [],
      ...payload,
    };
    clientProjects.unshift(saved);
    projects.unshift(saved);
    return ok(saved);
  }

  if (url.pathname === '/projects/client/my' && method === 'GET') {
    return ok(clientProjects.map((project) => ({
      ...project,
      dday: calcDday(project.deadline),
      applicantCount: project.applications.length,
    })));
  }

  const projectMatch = url.pathname.match(/^\/projects\/(\d+)$/);
  if (projectMatch && method === 'PUT') {
    const projectId = Number(projectMatch[1]);
    const project = clientProjects.find((item) => item.id === projectId);
    if (!project) throw new Error('프로젝트를 찾을 수 없습니다.');
    if (project.applications.length > 0) throw new Error('지원자가 있는 프로젝트는 수정할 수 없습니다.');
    Object.assign(project, parseBody(options));
    return ok(project);
  }
  if (projectMatch && method === 'GET') {
    const projectId = Number(projectMatch[1]);
    return ok(clientProjects.find((project) => project.id === projectId) || projects.find((project) => project.id === projectId));
  }

  const applicantMatch = url.pathname.match(/^\/projects\/(\d+)\/applicants$/);
  if (applicantMatch && method === 'GET') {
    const project = clientProjects.find((item) => item.id === Number(applicantMatch[1]));
    const page = Number(url.searchParams.get('page') || 0);
    const size = Number(url.searchParams.get('size') || 2);
    return ok(pageResponse(project?.applications || [], page, size));
  }

  if (url.pathname === '/member/profile' && method === 'GET') {
    return ok(profile);
  }

  if (url.pathname === '/member/profile' && method === 'PUT') {
    profile = { ...profile, ...parseBody(options) };
    return ok(profile);
  }

  if (url.pathname === '/member/profile/image' && method === 'POST') {
    const file = options.body?.get('image');
    const profileImage = file ? URL.createObjectURL(file) : '';
    profile = { ...profile, profileImage };
    return ok({ profileImage });
  }

  if (url.pathname === '/applications' && method === 'POST') {
    const payload = parseBody(options);
    const project = projects.find((item) => item.id === Number(payload.projectId));
    if (currentMockUser?.role !== 'DEVELOPER') throw new Error('개발자만 프로젝트에 지원할 수 있습니다.');
    if (project.status === 'CLOSED' || new Date(`${project.deadline}T23:59:59`) < new Date()) {
      throw new Error('마감된 프로젝트에는 지원할 수 없습니다.');
    }
    if (applications.some((item) => Number(item.projectId) === Number(payload.projectId))) {
      throw new Error('이미 지원한 프로젝트입니다.');
    }
    const saved = {
      id: Date.now(),
      projectId: project.id,
      projectTitle: project.title,
      projectSummary: project.description,
      type: project.type,
      applicantCount: project.applicantCount + 1,
      appliedAt: new Date().toISOString().slice(0, 10),
      status: 'PENDING',
      ...payload,
    };
    applications = [saved, ...applications];
    return ok(saved);
  }

  if (url.pathname === '/applications/my' && method === 'GET') {
    return ok(applications);
  }

  const applicationMatch = url.pathname.match(/^\/applications\/(\d+)$/);
  if (applicationMatch && method === 'GET') {
    const applicationId = Number(applicationMatch[1]);
    const clientApplication = clientProjects
      .flatMap((project) => project.applications)
      .find((item) => (item.applicationId ?? item.id) === applicationId);
    return ok(clientApplication || applications.find((item) => item.id === applicationId));
  }

  const acceptMatch = url.pathname.match(/^\/applications\/(\d+)\/accept$/);
  if (acceptMatch && method === 'PATCH') {
    const applicationId = Number(acceptMatch[1]);
    const application = clientProjects
      .flatMap((project) => project.applications)
      .find((item) => (item.applicationId ?? item.id) === applicationId);
    if (!application) throw new Error('지원서를 찾을 수 없습니다.');
    application.status = 'ACCEPTED';
    return ok(application);
  }

  throw new Error(`Mock API 미구현: ${method} ${path}`);
}
