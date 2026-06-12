import { useState } from 'react';
import AppliedProjectList from './AppliedProjectList.jsx';
import ProfileEditor from './ProfileEditor.jsx';

const menuItems = [
  { id: 'projects', label: '지원한 프로젝트', desc: '제출한 지원서 확인' },
  { id: 'profile', label: '프로필 관리', desc: '기본 정보와 검색태그 수정' },
];

export default function DeveloperMyPage() {
  const [tab, setTab] = useState('projects');

  return (
    <section>
      <div className="page-title-row">
        <div>
          <p className="eyebrow">Partner My Page</p>
          <h1>개발자 마이페이지</h1>
          <p className="subtitle">지원 현황과 프로필 정보를 한 곳에서 관리합니다.</p>
        </div>
      </div>

      <div className="mypage-layout">
        <aside className="mypage-sidebar">
          <h2>마이페이지</h2>
          {menuItems.map((item) => (
            <button
              type="button"
              className={tab === item.id ? 'side-menu active' : 'side-menu'}
              onClick={() => setTab(item.id)}
              key={item.id}
            >
              <strong>{item.label}</strong>
              <span>{item.desc}</span>
            </button>
          ))}
        </aside>

        <div className="mypage-content">
          {tab === 'projects' ? <AppliedProjectList /> : <ProfileEditor />}
        </div>
      </div>
    </section>
  );
}
