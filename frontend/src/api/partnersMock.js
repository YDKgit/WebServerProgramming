const partners = [
  {
    id: 1,
    name: '김개발',
    maskedName: '김개***',
    businessType: '개인 프리랜서',
    region: '서울',
    fields: ['개발'],
    introduction: 'Spring Boot 기반 백엔드와 대용량 트래픽 시스템 구축 경험을 보유한 개발자입니다.',
    skills: ['Java', 'Spring Boot', 'JPA', 'MySQL', 'Docker'],
    identityVerified: true,
    contactVerified: true,
    available: true,
    onsiteAvailable: false,
    recentlyActive: true,
    favoriteCount: 52,
    rating: 4.9,
    reviewCount: 12,
    contractCount: 18,
    portfolioCount: 8,
    totalAmount: 12800,
    hasVideo: true,
    avatarColor: '#e8f1ff',
  },
  {
    id: 2,
    name: '이자바',
    maskedName: '이자***',
    businessType: '개인 프리랜서',
    region: '경기',
    fields: ['기획', '개발'],
    introduction: '서비스 기획 단계부터 API 설계, 배포까지 함께할 수 있는 7년차 백엔드 개발자입니다.',
    skills: ['Java', 'Spring', 'Kafka', 'Redis', 'AWS'],
    identityVerified: true,
    contactVerified: true,
    available: true,
    onsiteAvailable: true,
    recentlyActive: true,
    favoriteCount: 37,
    rating: 4.8,
    reviewCount: 9,
    contractCount: 14,
    portfolioCount: 5,
    totalAmount: 9850,
    hasVideo: false,
    avatarColor: '#fff1e8',
  },
  {
    id: 3,
    name: '최풀스택',
    maskedName: '최풀***',
    businessType: '개인사업자',
    region: '부산',
    fields: ['디자인', '개발'],
    introduction: '사용자 중심 UI 설계와 React, Spring Boot 기반 서비스 개발을 함께 제공합니다.',
    skills: ['React', 'TypeScript', 'Spring', 'Figma', 'MySQL'],
    identityVerified: true,
    contactVerified: true,
    available: true,
    onsiteAvailable: true,
    recentlyActive: true,
    favoriteCount: 29,
    rating: 4.7,
    reviewCount: 8,
    contractCount: 11,
    portfolioCount: 14,
    totalAmount: 7640,
    hasVideo: true,
    avatarColor: '#edf9f1',
  },
  {
    id: 4,
    name: '모아디자인',
    maskedName: '모아디***',
    businessType: '팀 프리랜서',
    region: '서울',
    fields: ['기획', '디자인'],
    introduction: '브랜드 경험 설계부터 웹·앱 UI 디자인까지 수행하는 4인 전문 디자인 팀입니다.',
    skills: ['UX Research', 'Figma', 'Illustrator', 'Design System'],
    identityVerified: true,
    contactVerified: true,
    available: false,
    onsiteAvailable: false,
    recentlyActive: true,
    favoriteCount: 41,
    rating: 4.9,
    reviewCount: 16,
    contractCount: 22,
    portfolioCount: 31,
    totalAmount: 14320,
    hasVideo: false,
    avatarColor: '#f7edff',
  },
  {
    id: 5,
    name: '테크브릿지',
    maskedName: '테크브***',
    businessType: '법인사업자',
    region: '대구',
    fields: ['기획', '디자인', '개발'],
    introduction: '기업용 웹 서비스 구축과 운영 자동화를 전문으로 하는 검증된 IT 개발사입니다.',
    skills: ['Cloud', 'Kubernetes', 'React', 'Spring Boot', 'DevOps'],
    identityVerified: true,
    contactVerified: true,
    available: true,
    onsiteAvailable: true,
    recentlyActive: false,
    favoriteCount: 68,
    rating: 4.6,
    reviewCount: 21,
    contractCount: 37,
    portfolioCount: 26,
    totalAmount: 28900,
    hasVideo: true,
    avatarColor: '#eef2f7',
  },
];

export function getMockPartners({
  keyword = '',
  infoType = 'basic',
  businessTypes = [],
  onsiteOnly = false,
  activeOnly = false,
  fields = [],
  region = 'ALL',
  sort = 'default',
} = {}) {
  const normalizedKeyword = keyword.trim().toLowerCase();
  let filtered = partners.filter((partner) => {
    const searchable = [
      partner.name,
      partner.businessType,
      partner.region,
      partner.introduction,
      ...partner.fields,
      ...partner.skills,
    ].join(' ').toLowerCase();

    if (normalizedKeyword && !searchable.includes(normalizedKeyword)) return false;
    if (infoType === 'portfolio' && partner.portfolioCount === 0) return false;
    if (infoType === 'video' && !partner.hasVideo) return false;
    if (businessTypes.length && !businessTypes.includes(partner.businessType)) return false;
    if (onsiteOnly && !partner.onsiteAvailable) return false;
    if (activeOnly && !partner.recentlyActive) return false;
    if (fields.length && !fields.some((field) => partner.fields.includes(field))) return false;
    if (region !== 'ALL' && partner.region !== region) return false;
    return true;
  });

  filtered = [...filtered].sort((a, b) => {
    if (sort === 'rating') return b.rating - a.rating;
    if (sort === 'contracts') return b.contractCount - a.contractCount;
    if (sort === 'portfolio') return b.portfolioCount - a.portfolioCount;
    return b.favoriteCount - a.favoriteCount;
  });

  return Promise.resolve({
    content: filtered,
    totalElements: filtered.length,
  });
}
