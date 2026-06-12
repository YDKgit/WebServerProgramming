import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { developerApi, projectApi } from '../../api/api';
import ErrorBox from '../common/ErrorBox.jsx';
import Loading from '../common/Loading.jsx';
import { calcDday, formatMoney, formatStatus, formatType, isProjectClosed, toProjectView } from '../../utils/format';

export default function ProjectDetailPage() {
  const loginUser = JSON.parse(localStorage.getItem('loginUser') || 'null');
  const { projectId } = useParams();
  const [project, setProject] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [applied, setApplied] = useState(false);

  useEffect(() => {
    async function loadProject() {
      setLoading(true);
      setError('');
      try {
        const detail = await projectApi.getProject(projectId);
        setProject(detail);
        if (loginUser?.role === 'DEVELOPER') {
          const applications = await developerApi.getAppliedProjects();
          setApplied(applications.some((application) => Number(application.projectId) === Number(projectId)));
        }
      } catch (event) {
        setError(event.message);
      } finally {
        setLoading(false);
      }
    }
    loadProject();
  }, [projectId]);

  if (loading) return <Loading message="프로젝트 상세 정보를 불러오는 중입니다." />;
  if (error) return <ErrorBox message={error} />;
  if (!project) return <ErrorBox message="프로젝트를 찾을 수 없습니다." />;
  const item = toProjectView(project);
  const closed = isProjectClosed(item);
  const isDeveloper = loginUser?.role === 'DEVELOPER';

  return (
    <section>
      <div className="detail-hero">
        <div>
          <div className="card-topline">
            <span className="badge primary">{formatType(item.type)}</span>
            <span className={item.status === 'RECRUITING' ? 'badge success' : 'badge muted'}>
              {formatStatus(item.status)}
            </span>
            <span className="deadline-chip">{calcDday(item.deadline)}</span>
          </div>
          <h1>{item.title}</h1>
          <p className="subtitle">{item.description}</p>
          <div className="tag-list">
            {(item.skills || []).map((skill) => <span className="tag" key={skill}>{skill}</span>)}
          </div>
        </div>
        {isDeveloper && (
          closed ? (
            <button type="button" className="btn disabled-action big" disabled>마감</button>
          ) : applied ? (
            <button type="button" className="btn completed-action big" disabled>지원 완료</button>
          ) : (
            <Link to={`/projects/${item.id}/apply`} className="btn primary big">지원하기</Link>
          )
        )}
      </div>

      <div className="two-column">
        <section className="panel">
          <h2>프로젝트 요약</h2>
          <dl className="info-list">
            <div><dt>계약 형태</dt><dd>{formatType(item.type)}</dd></div>
            <div><dt>{item.type === 'RESIDENT' ? '월 급여' : '예상 비용'}</dt><dd>{formatMoney(item.budget, item.type)}</dd></div>
            <div><dt>예상 기간</dt><dd>{item.expectedPeriod ? `${item.expectedPeriod}일` : '-'}</dd></div>
            <div><dt>지원자 수</dt><dd>{item.applicantCount ?? 0}명</dd></div>
            <div><dt>모집 마감</dt><dd>{item.deadline || '-'} · {calcDday(item.deadline)}</dd></div>
          </dl>
        </section>

        <section className="panel">
          <h2>의뢰 내용</h2>
          <dl className="info-list">
            <div><dt>프로젝트 분야</dt><dd>{(item.fields || []).join(', ') || '-'}</dd></div>
            <div><dt>기획 상태</dt><dd>{item.planningStatus || '-'}</dd></div>
            <div><dt>미팅 희망 지역</dt><dd>{item.meetingRegion || '-'}</dd></div>
            <div><dt>진행 방식</dt><dd>{item.workMethod || '-'}</dd></div>
          </dl>
        </section>
      </div>
    </section>
  );
}
