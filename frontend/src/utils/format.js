export function formatMoney(value, type) {
  if (value === undefined || value === null || value === '') return '-';
  const number = Number(value);
  if (Number.isNaN(number)) return String(value);
  const manwon = Math.abs(number) >= 100000 ? Math.round(number / 10000) : number;
  return type === 'RESIDENT' ? `월 ${manwon.toLocaleString()}만원` : `${manwon.toLocaleString()}만원`;
}

export function formatType(type) {
  if (type === 'RESIDENT') return '상주';
  if (type === 'OUTSOURCING') return '도급';
  if (type === 'web') return '웹';
  if (type === 'app') return '앱';
  return type || '-';
}

export function formatStatus(status) {
  if (status === 'RECRUITING') return '모집중';
  if (status === 'CLOSED') return '마감';
  return status || '-';
}

export function formatApplicationStatus(status) {
  if (status === 'ACCEPTED') return '수락 완료';
  if (status === 'REJECTED') return '거절';
  if (status === 'PENDING' || status === '검토중') return '검토중';
  return status || '검토중';
}

export function formatDateTimeMinute(value) {
  if (!value) return '-';
  const normalized = String(value).replace('T', ' ');
  const match = normalized.match(/^(\d{4}-\d{2}-\d{2})[ ](\d{2}:\d{2})/);
  return match ? `${match[1]} ${match[2]}` : normalized;
}

export function isProjectClosed(project = {}) {
  const status = project.status || project.recruitStatus;
  if (status === 'CLOSED') return true;
  if (!project.deadline) return false;

  const deadline = new Date(`${project.deadline}T23:59:59`);
  return deadline.getTime() < Date.now();
}

export function addDays(days) {
  const date = new Date();
  date.setDate(date.getDate() + days);
  return date.toISOString().slice(0, 10);
}

export function calcDday(deadline) {
  if (!deadline) return '-';
  const today = new Date();
  const end = new Date(deadline);
  today.setHours(0, 0, 0, 0);
  end.setHours(0, 0, 0, 0);
  const diff = Math.ceil((end - today) / (1000 * 60 * 60 * 24));
  if (diff === 0) return 'D-day';
  if (diff > 0) return `D-${diff}`;
  return `D+${Math.abs(diff)}`;
}

export function hasContactInfo(text) {
  const emailPattern = /[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}/i;
  const phonePattern = /(01[016789])[-\s.]?\d{3,4}[-\s.]?\d{4}|\d{2,3}[-\s.]?\d{3,4}[-\s.]?\d{4}/;
  return emailPattern.test(text) || phonePattern.test(text);
}

export function getPageItems(pageData) {
  if (Array.isArray(pageData)) return pageData;
  return pageData?.content || pageData?.items || pageData?.data || [];
}

export function getImageUrl(value) {
  if (!value) return '';
  const imagePath = typeof value === 'string'
    ? value
    : value.imageUrl || value.profileImage || value.filePath || value.path || '';

  if (!imagePath || /^(https?:|blob:|data:)/i.test(imagePath)) return imagePath;

  const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
  const serverOrigin = apiBaseUrl.replace(/\/api\/?$/, '');
  return `${serverOrigin}${imagePath.startsWith('/') ? imagePath : `/${imagePath}`}`;
}

export function splitCsv(value) {
  if (Array.isArray(value)) return value;
  if (!value) return [];
  return String(value)
    .split(',')
    .map((item) => item.trim())
    .filter(Boolean);
}

export function toProjectView(project = {}) {
  const employmentType = project.type || project.employmentType;
  const normalizedType = employmentType === '상주' || employmentType === 'RESIDENT' ? 'RESIDENT' : 'OUTSOURCING';
  const status = project.status || project.recruitStatus;

  return {
    ...project,
    id: project.id,
    title: project.title || '',
    description: project.description || project.workContent || '',
    skills: splitCsv(project.skills || project.techStack || project.requiredSkills),
    fields: splitCsv(project.fields || project.participationFields || project.category),
    type: normalizedType,
    status,
    budget: project.budget,
    expectedPeriod: project.expectedPeriod || project.estimatedDuration,
    deadline: project.deadline,
    applicantCount: project.applicantCount ?? 0,
    clientId: project.clientId,
    planningStatus: project.planningStatus,
    meetingRegion: project.meetingRegion,
    workMethod: project.workMethod || project.workType,
  };
}
