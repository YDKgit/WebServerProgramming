import { useState } from 'react';
import { Link } from 'react-router-dom';
import {
  calcDday,
  formatMoney,
  formatStatus,
  formatType,
  isProjectClosed,
  toProjectView,
} from '../../utils/format';

export default function ProjectCard({ project, loginUser, applied = false, appliedReady = true }) {
  const [favorite, setFavorite] = useState(false);
  const item = toProjectView(project);
  const skills = item.skills || [];
  const fields = item.fields || [];
  const closed = isProjectClosed(item);
  const isDeveloper = loginUser?.role === 'DEVELOPER';
  const isOwnClientProject = loginUser?.role === 'CLIENT'
    && Number(item.clientId) === Number(loginUser.id);
  const detailPath = isOwnClientProject
    ? `/client/projects/${item.id}`
    : `/projects/${item.id}`;

  return (
    <article className="project-card freemoa-project-card">
      <div className="project-card-heading">
        <div>
          <h2>{item.title}</h2>
          <div className="project-field-line">
            {(fields.length ? fields : ['개발']).map((field) => (
              <span key={field}>{field}</span>
            ))}
            {skills.map((skill) => <b key={skill}>{skill}</b>)}
          </div>
        </div>
        <div className="project-card-badges">
          <span className="project-type-pill">{item.type === 'RESIDENT' ? '기간제 상주' : formatType(item.type)}</span>
          <span className={item.status === 'RECRUITING' ? 'recruit-pill' : 'closed-pill'}>
            {formatStatus(item.status)}
          </span>
          <button
            type="button"
            className={favorite ? 'favorite-button active' : 'favorite-button'}
            onClick={() => setFavorite((current) => !current)}
            aria-label="관심 프로젝트"
          >
            {favorite ? '♥' : '♡'}
          </button>
        </div>
      </div>

      <dl className="project-summary-bar">
        <div>
          <dt>{item.type === 'RESIDENT' ? '월임금' : '예상비용'}</dt>
          <dd>{formatMoney(item.budget, item.type)}</dd>
        </div>
        <div><dt>예상기간</dt><dd>{item.expectedPeriod ? `${item.expectedPeriod}일` : '-'}</dd></div>
        <div><dt>지원자수</dt><dd>{item.applicantCount ?? 0}명</dd></div>
        <div><dt>마감일정</dt><dd>{calcDday(item.deadline)}</dd></div>
      </dl>

      <div className="project-card-bottom">
        <p>{item.description || '프로젝트 상세 내용은 상세보기에서 확인할 수 있습니다.'}</p>
        <div className="project-card-client">
          <span className="client-avatar">F</span>
          <div>
            <strong>검증된 의뢰인</strong>
            <small>{item.meetingRegion || '온라인'} · 연락처 인증</small>
          </div>
        </div>
      </div>

      {(isDeveloper || isOwnClientProject) && (
        <div className="project-card-actions">
          <Link to={detailPath} className="btn secondary">상세보기</Link>
          {isDeveloper && (
            !appliedReady ? (
              <button type="button" className="btn disabled-action" disabled>확인 중</button>
            ) : closed ? (
              <button type="button" className="btn disabled-action" disabled>지원 불가</button>
            ) : applied ? (
              <button type="button" className="btn completed-action" disabled>지원 완료</button>
            ) : (
              <Link to={`/projects/${item.id}/apply`} className="btn primary">지원하기</Link>
            )
          )}
        </div>
      )}
    </article>
  );
}
