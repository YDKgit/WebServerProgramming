import { Link } from 'react-router-dom';
import { calcDday, formatMoney, formatStatus, formatType, isProjectClosed, toProjectView } from '../../utils/format';

export default function ProjectCard({ project, loginUser, applied = false, appliedReady = true }) {
  const item = toProjectView(project);
  const skills = item.skills || [];
  const closed = isProjectClosed(item);
  const isDeveloper = loginUser?.role === 'DEVELOPER';
  const isOwnClientProject = loginUser?.role === 'CLIENT'
    && Number(item.clientId) === Number(loginUser.id);
  const detailPath = isOwnClientProject
    ? `/client/projects/${item.id}`
    : `/projects/${item.id}`;

  return (
    <article className="project-card">
      <div className="project-card-main">
        <div className="card-topline">
          <span className="badge primary">{formatType(item.type)}</span>
          <span className={item.status === 'RECRUITING' ? 'badge success' : 'badge muted'}>
            {formatStatus(item.status)}
          </span>
          <span className="deadline-chip">{calcDday(item.deadline)}</span>
        </div>

        <h3>{item.title}</h3>
        {item.description && <p className="summary-text">{item.description}</p>}

        <div className="tag-list">
          {skills.length > 0 ? (
            skills.map((skill) => <span className="tag" key={skill}>{skill}</span>)
          ) : (
            <span className="tag">기술 협의</span>
          )}
        </div>
      </div>

      <dl className="project-meta">
        <div>
          <dt>{item.type === 'RESIDENT' ? '월 급여' : '예상 비용'}</dt>
          <dd>{formatMoney(item.budget, item.type)}</dd>
        </div>
        <div>
          <dt>예상 기간</dt>
          <dd>{item.expectedPeriod ? `${item.expectedPeriod}일` : '-'}</dd>
        </div>
        <div>
          <dt>지원자 수</dt>
          <dd>{item.applicantCount ?? 0}명</dd>
        </div>
        <div>
          <dt>마감일</dt>
          <dd>{item.deadline || '-'}</dd>
        </div>
      </dl>

      {(isDeveloper || isOwnClientProject) && (
        <div className="card-actions">
          <Link to={detailPath} className="btn secondary">상세보기</Link>
          {isDeveloper && (
            !appliedReady ? (
              <button type="button" className="btn disabled-action" disabled>확인 중</button>
            ) : closed ? (
              <button type="button" className="btn disabled-action" disabled>마감</button>
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
