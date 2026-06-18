import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { projectApi } from '../../api/api';
import { calcDday, formatMoney, formatType, toProjectView } from '../../utils/format';
import ErrorBox from '../common/ErrorBox.jsx';
import Loading from '../common/Loading.jsx';

export default function ClientMyPage() {
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadProjects = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      setProjects((await projectApi.getClientProjects()).map(toProjectView));
    } catch (event) {
      console.error('의뢰 프로젝트 조회 실패:', event);
      setError(event.message || '프로젝트 목록을 불러오지 못했습니다.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadProjects();
  }, [loadProjects]);

  return (
    <section>
      <div className="page-title-row">
        <div>
          <p className="eyebrow">Client My Page</p>
          <h1>의뢰인 마이페이지</h1>
          <p className="subtitle">프로젝트 의뢰와 지원자 현황을 한 곳에서 관리합니다.</p>
        </div>
      </div>

      <div className="mypage-layout client-projects-only-layout">
        <div className="mypage-content">
            <section className="panel">
              <div className="panel-title">
                <div>
                  <h2>내가 의뢰한 프로젝트</h2>
                  <p>상세 페이지에서 의뢰 내용과 지원자를 확인하고 관리할 수 있습니다.</p>
                </div>
                <Link to="/client-project-form" className="btn primary">새 프로젝트 의뢰</Link>
              </div>

              {error && <ErrorBox message={error} />}
              {loading ? (
                <Loading message="의뢰 프로젝트를 불러오는 중입니다." />
              ) : projects.length === 0 ? (
                <div className="empty-box">등록한 프로젝트가 없습니다.</div>
              ) : (
                <div className="list-stack">
                  {projects.map((project) => (
                    <article className="mini-card client-project-list-card" key={project.id}>
                      <div>
                        <div className="card-topline">
                          <span className="badge primary">{formatType(project.type)}</span>
                          <span className={project.status === 'RECRUITING' ? 'badge success' : 'badge muted'}>
                            {project.status === 'RECRUITING' ? '모집중' : '마감'}
                          </span>
                        </div>
                        <h3>{project.title}</h3>
                        <p>{project.description || (project.skills || []).join(', ') || '등록된 프로젝트 의뢰입니다.'}</p>
                      </div>
                      <dl className="compact-meta">
                        <div><dt>예상 금액</dt><dd>{formatMoney(project.budget, project.type)}</dd></div>
                        <div><dt>지원자</dt><dd>{project.applicantCount ?? 0}명</dd></div>
                        <div><dt>모집 마감</dt><dd>{project.deadline || '-'} · {calcDday(project.deadline)}</dd></div>
                      </dl>
                      <Link to={`/client/projects/${project.id}`} className="btn secondary">
                        상세 보기
                      </Link>
                    </article>
                  ))}
                </div>
              )}
            </section>
        </div>
      </div>
    </section>
  );
}
