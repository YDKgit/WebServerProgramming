import { useEffect, useState } from 'react';
import { developerApi } from '../../api/api';
import ErrorBox from '../common/ErrorBox.jsx';
import Loading from '../common/Loading.jsx';
import { formatApplicationStatus, formatDateTimeMinute, formatMoney } from '../../utils/format';

function applicationIdOf(application) {
  return application.applicationId || application.id;
}

export default function AppliedProjectList() {
  const [applications, setApplications] = useState([]);
  const [selected, setSelected] = useState(null);
  const [loading, setLoading] = useState(true);
  const [detailLoading, setDetailLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    async function loadApplications() {
      setLoading(true);
      setError('');
      try {
        setApplications(await developerApi.getAppliedProjects());
      } catch (event) {
        setError(event.message);
      } finally {
        setLoading(false);
      }
    }
    loadApplications();
  }, []);

  const openDetail = async (application) => {
    setDetailLoading(true);
    setError('');
    try {
      const detail = await developerApi.getApplication(applicationIdOf(application));
      setSelected(detail || application);
    } catch (event) {
      setError(event.message);
    } finally {
      setDetailLoading(false);
    }
  };

  if (loading) return <Loading message="지원한 프로젝트를 불러오는 중입니다." />;
  if (error && applications.length === 0) return <ErrorBox message={error} />;

  return (
    <section className="panel">
      <div className="panel-title">
        <div>
          <h2>지원한 프로젝트</h2>
          <p>내가 제출한 지원서와 견적 내용을 확인합니다.</p>
        </div>
      </div>

      {error && <ErrorBox message={error} />}
      {detailLoading && <Loading message="지원서 상세를 불러오는 중입니다." />}

      {applications.length === 0 ? (
        <div className="empty-box">아직 지원한 프로젝트가 없습니다.</div>
      ) : (
        <div className="list-stack">
          {applications.map((application) => (
            <article className="mini-card" key={applicationIdOf(application)}>
              <div>
                <span className={application.status === 'ACCEPTED' ? 'badge success' : 'badge primary'}>
                  {formatApplicationStatus(application.status)}
                </span>
                <h3>{application.projectTitle}</h3>
                <p>프로젝트 #{application.projectId}</p>
              </div>
              <dl className="compact-meta">
                <div><dt>견적</dt><dd>{formatMoney(application.bidAmount, 'OUTSOURCING')}</dd></div>
                <div><dt>지원일</dt><dd>{formatDateTimeMinute(application.appliedAt)}</dd></div>
              </dl>
              <button type="button" className="btn secondary" onClick={() => openDetail(application)}>지원서 보기</button>
            </article>
          ))}
        </div>
      )}

      {selected && (
        <div className="modal-backdrop" onClick={() => setSelected(null)}>
          <div className="modal" onClick={(event) => event.stopPropagation()}>
            <div className="modal-head">
              <h3>{selected.projectTitle}</h3>
              <button type="button" onClick={() => setSelected(null)}>×</button>
            </div>
            <dl className="info-list">
              <div><dt>개발자</dt><dd>{selected.developerName || '-'}</dd></div>
              <div><dt>이메일</dt><dd>{selected.email || '-'}</dd></div>
              <div><dt>전화번호</dt><dd>{selected.phone || '-'}</dd></div>
              <div><dt>지원 금액</dt><dd>{formatMoney(selected.bidAmount, 'OUTSOURCING')}</dd></div>
              <div><dt>작업 기간</dt><dd>{selected.workDuration ? `${selected.workDuration}일` : '-'}</dd></div>
              <div><dt>지원일</dt><dd>{formatDateTimeMinute(selected.appliedAt)}</dd></div>
            </dl>
            <h4>지원서 내용</h4>
            <div className="application-content">{selected.proposalContent || '-'}</div>
          </div>
        </div>
      )}
    </section>
  );
}
