import { useState } from 'react';

const businessOptions = ['개인 프리랜서', '팀 프리랜서', '개인사업자', '법인사업자'];
const fieldOptions = ['기획', '디자인', '개발'];
const regionOptions = ['ALL', '서울', '경기', '부산', '대구'];

export default function PartnersFilterSidebar({ filters, onChange, onToggleArray }) {
  const [regionOpen, setRegionOpen] = useState(false);

  return (
    <aside className="market-filter-sidebar partners-filter-sidebar">
      <div className="market-filter-title">⌕&nbsp; 파트너스 필터</div>

      <section className="market-filter-section highlighted-filter-section">
        {[
          ['basic', '기본 정보'],
          ['portfolio', '포트폴리오'],
          ['video', '동영상 인터뷰'],
        ].map(([value, label]) => (
          <label className="market-check-row" key={value}>
            <input
              type="radio"
              name="infoType"
              value={value}
              checked={filters.infoType === value}
              onChange={(event) => onChange('infoType', event.target.value)}
            />
            <span>{label}</span>
          </label>
        ))}
      </section>

      <section className="market-filter-section">
        <h2>구성분류 <small>중복선택가능</small></h2>
        {businessOptions.map((option) => (
          <label className="market-check-row" key={option}>
            <input
              type="checkbox"
              checked={filters.businessTypes.includes(option)}
              onChange={() => onToggleArray('businessTypes', option)}
            />
            <span>{option}</span>
          </label>
        ))}
      </section>

      <section className="market-filter-section">
        <label className="market-check-row">
          <input
            type="checkbox"
            checked={filters.onsiteOnly}
            onChange={(event) => onChange('onsiteOnly', event.target.checked)}
          />
          <span>상주 가능만 보기</span>
        </label>
        <label className="market-check-row">
          <input
            type="checkbox"
            checked={filters.activeOnly}
            onChange={(event) => onChange('activeOnly', event.target.checked)}
          />
          <span>최근 활동중만 보기</span>
        </label>
      </section>

      <section className="market-filter-section">
        <h2>참여분야 <small>중복선택가능</small></h2>
        <label className="market-check-row">
          <input
            type="checkbox"
            checked={filters.fields.length === 0}
            onChange={() => onChange('fields', [])}
          />
          <span>전체</span>
        </label>
        {fieldOptions.map((option) => (
          <label className="market-check-row" key={option}>
            <input
              type="checkbox"
              checked={filters.fields.includes(option)}
              onChange={() => onToggleArray('fields', option)}
            />
            <span>{option}</span>
          </label>
        ))}
      </section>

      <section className="market-filter-section accordion-section">
        <button type="button" className="accordion-toggle" onClick={() => setRegionOpen((open) => !open)}>
          <span>지역검색</span>
          <span>{regionOpen ? '⌃' : '⌄'}</span>
        </button>
        {regionOpen && (
          <select value={filters.region} onChange={(event) => onChange('region', event.target.value)}>
            {regionOptions.map((option) => (
              <option value={option} key={option}>{option === 'ALL' ? '전체 지역' : option}</option>
            ))}
          </select>
        )}
      </section>
    </aside>
  );
}
