import { useState } from 'react';

const participationOptions = [
  '기획',
  '디자인',
  '개발',
  '기획+개발',
  '디자인+개발',
  '기획+디자인',
  '기획+디자인+개발',
];

const regionOptions = ['ALL', '서울', '경기', '부산', '대구', '온라인'];

export default function ProjectFilterSidebar({
  employmentType,
  participation,
  region,
  status,
  onEmploymentTypeChange,
  onParticipationChange,
  onRegionChange,
  onStatusChange,
}) {
  const [regionOpen, setRegionOpen] = useState(false);

  return (
    <aside className="market-filter-sidebar">
      <div className="market-filter-title">프로젝트 필터</div>
      <div className="filter-persist-note">✓ 체크된 필터 항상 적용</div>

      <section className="market-filter-section">
        <h2>프로젝트 형태</h2>
        {[
          ['ALL', '전체'],
          ['OUTSOURCING', '도급(원격)'],
          ['RESIDENT', '상주'],
        ].map(([value, label]) => (
          <label className="market-check-row" key={value}>
            <input
              type="radio"
              name="employmentType"
              value={value}
              checked={employmentType === value}
              onChange={(event) => onEmploymentTypeChange(event.target.value)}
            />
            <span>{label}</span>
          </label>
        ))}
      </section>

      <section className="market-filter-section">
        <h2>참여파트 분류 <small>중복선택가능</small></h2>
        {participationOptions.map((option) => (
          <label className="market-check-row" key={option}>
            <input
              type="checkbox"
              checked={participation.includes(option)}
              onChange={() => onParticipationChange(option)}
            />
            <span>{option}</span>
          </label>
        ))}
      </section>

      <section className="market-filter-section">
        <h2>모집 상태</h2>
        {[
          ['ALL', '전체'],
          ['RECRUITING', '모집중'],
          ['CLOSED', '마감'],
        ].map(([value, label]) => (
          <label className="market-check-row" key={value}>
            <input
              type="radio"
              name="projectStatus"
              value={value}
              checked={status === value}
              onChange={(event) => onStatusChange(event.target.value)}
            />
            <span>{label}</span>
          </label>
        ))}
      </section>

      <section className="market-filter-section accordion-section">
        <button type="button" className="accordion-toggle" onClick={() => setRegionOpen((open) => !open)}>
          <span>지역검색</span>
          <span>{regionOpen ? '⌃' : '⌄'}</span>
        </button>
        {regionOpen && (
          <select value={region} onChange={(event) => onRegionChange(event.target.value)}>
            {regionOptions.map((option) => (
              <option value={option} key={option}>{option === 'ALL' ? '전체 지역' : option}</option>
            ))}
          </select>
        )}
      </section>
    </aside>
  );
}
