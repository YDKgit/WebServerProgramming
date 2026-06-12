import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { applicationApi, projectApi } from '../../api/api';
import {
  formatApplicationStatus,
  formatDateTimeMinute,
  formatMoney,
  formatStatus,
  formatType,
  getPageItems,
  toProjectView,
} from '../../utils/format';
import ErrorBox from '../common/ErrorBox.jsx';
import Loading from '../common/Loading.jsx';

const APPLICANT_PAGE_SIZE = 2;

function formatExpectedPeriod(value) {
  if (value === undefined || value === null || value === '') return '-';
  const text = String(value);
  return /[일주개월]/.test(text) ? text : `${text}일`;
}

export default function ClientProjectDetailPage() {
  const { projectId } = useParams();
  const [project, setProject] = useState(null);
  const [applicants, setApplicants] = useState([]);
  const [applicantPage, setApplicantPage] = useState(0);
  const [applicantLast, setApplicantLast] = useState(true);
  const [selectedApplication, setSelectedApplication] = useState(null);
  const [projectForm, setProjectForm] = useState(null);
  const [editing, setEditing] = useState(false);
  const [loading, setLoading] = useState(true);
  const [applicantLoading, setApplicantLoading] = useState(false);
  const [applicationLoading, setApplicationLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [acceptingId, setAcceptingId] = useState(null);
  const [error, setError] = useState('');
  const [applicantError, setApplicantError] = useState('');
  const [applicationError, setApplicationError] = useState('');

  const applyApplicantPage = (result, page, append = false) => {
    const items = getPageItems(result);
    setApplicants((current) => {
      if (!append) return items;
      const knownIds = new Set(current.map((item) => item.applicationId ?? item.id));
      return [...current, ...items.filter((item) => !knownIds.has(item.applicationId ?? item.id))];
    });
    setApplicantPage(page);
    setApplicantLast(Boolean(result?.last ?? items.length < APPLICANT_PAGE_SIZE));
  };

  useEffect(() => {
    let ignore = false;

    async function loadDetail() {
      setLoading(true);
      setError('');
      setApplicantError('');

      const [detailResult, applicantResult] = await Promise.allSettled([
        projectApi.getClientProjectDetail(projectId),
        projectApi.getApplicants(projectId, { page: 0, size: APPLICANT_PAGE_SIZE }),
      ]);

      if (ignore) return;

      if (detailResult.status === 'fulfilled') {
        setProject(toProjectView(detailResult.value));
      } else {
        console.error('프로젝트 상세 조회 실패:', detailResult.reason);
        setError(detailResult.reason?.message || '프로젝트 상세 정보를 불러오지 못했습니다.');
      }

      if (applicantResult.status === 'fulfilled') {
        applyApplicantPage(applicantResult.value, 0);
      } else {
        console.error('지원자 목록 조회 실패:', applicantResult.reason);
        setApplicantError(applicantResult.reason?.message || '지원자 목록을 불러오지 못했습니다.');
      }

      setLoading(false);
    }

    loadDetail();
    return () => {
      ignore = true;
    };
  }, [projectId]);

  const loadMoreApplicants = async () => {
    setApplicantLoading(true);
    setApplicantError('');
    try {
      const nextPage = applicantPage + 1;
      const result = await projectApi.getApplicants(projectId, {
        page: nextPage,
        size: APPLICANT_PAGE_SIZE,
      });
      applyApplicantPage(result, nextPage, true);
    } catch (event) {
      console.error('지원자 목록 더보기 실패:', event);
      setApplicantError(event.message || '지원자 목록을 추가로 불러오지 못했습니다.');
    } finally {
      setApplicantLoading(false);
    }
  };

  const openApplication = async (application) => {
    const applicationId = application.applicationId ?? application.id;
    setSelectedApplication(null);
    setApplicationLoading(true);
    setApplicationError('');
    try {
      setSelectedApplication(await applicationApi.getApplication(applicationId));
    } catch (event) {
      console.error('지원서 상세 조회 실패:', event);
      setApplicationError(event.message || '지원서 내용을 불러오지 못했습니다.');
    } finally {
      setApplicationLoading(false);
    }
  };

  const acceptApplication = async (application) => {
    const applicationId = application.applicationId ?? application.id;
    setAcceptingId(applicationId);
    setApplicantError('');
    try {
      const accepted = await applicationApi.acceptApplication(applicationId);
      const status = accepted.status || 'ACCEPTED';
      setApplicants((current) => current.map((item) => (
        (item.applicationId ?? item.id) === applicationId ? { ...item, status } : item
      )));
      setSelectedApplication((current) => (
        current && (current.applicationId ?? current.id) === applicationId
          ? { ...current, status }
          : current
      ));
    } catch (event) {
      console.error('지원자 수락 실패:', event);
      setApplicantError(event.message || '지원자를 수락하지 못했습니다.');
    } finally {
      setAcceptingId(null);
    }
  };

  const openEditForm = () => {
    setProjectForm({
      title: project.title || '',
      deadline: project.deadline || '',
      employmentType: project.type || 'OUTSOURCING',
      budget: project.budget ?? '',
      workContent: project.description || '',
      requiredSkills: (project.skills || []).join(', '),
      estimatedDuration: project.expectedPeriod ?? '',
      workType: project.workMethod || '',
      startDate: project.startDate || '',
    });
    setEditing(true);
    setError('');
  };

  const changeProjectForm = (event) => {
    const { name, value } = event.target;
    setProjectForm((current) => ({ ...current, [name]: value }));
  };

  const updateProject = async (event) => {
    event.preventDefault();
    setSaving(true);
    setError('');
    try {
      const updated = await projectApi.updateProject(projectId, {
        ...projectForm,
        budget: Number(projectForm.budget),
        estimatedDuration: Number(projectForm.estimatedDuration) || null,
        startDate: projectForm.startDate || null,
      });
      setProject(toProjectView(updated));
      setEditing(false);
    } catch (event) {
      console.error('프로젝트 수정 실패:', event);
      setError(event.message || '프로젝트를 수정하지 못했습니다.');
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <Loading message="프로젝트 상세와 지원자 정보를 불러오는 중입니다." />;
  if (error && !project) return <ErrorBox message={error} />;
  if (!project) return <ErrorBox message="프로젝트를 찾을 수 없습니다." />;

  const hasApplicants = (project.applicantCount ?? applicants.length) > 0;

  return (
    <section className="client-project-page">
      <div className="client-detail-nav">
        <Link to="/client/mypage?tab=projects" className="btn text">← 프로젝트 관리로 돌아가기</Link>
      </div>

      <section className="panel client-project-detail">
        <div className="client-detail-heading">
          <div>
            <p className="eyebrow">Project Summary</p>
            <h1>프로젝트 의뢰내용 요약</h1>
          </div>
          {!hasApplicants && !editing && (
            <button type="button" className="btn secondary" onClick={openEditForm}>
              수정하기
            </button>
          )}
        </div>

        {error && <ErrorBox message={error} />}

        <dl className="info-list client-project-summary">
          <div><dt>프로젝트명</dt><dd>{project.title || '-'}</dd></div>
          <div><dt>모집 마감일</dt><dd>{project.deadline || '-'}</dd></div>
          <div><dt>예상 금액</dt><dd>{formatMoney(project.budget, project.type)}</dd></div>
          <div><dt>고용 형태</dt><dd>{formatType(project.type)}</dd></div>
          <div><dt>진행 방식</dt><dd>{project.workMethod || '-'}</dd></div>
          <div><dt>예상 기간</dt><dd>{formatExpectedPeriod(project.expectedPeriod)}</dd></div>
          <div><dt>모집 상태</dt><dd>{formatStatus(project.status)}</dd></div>
          <div><dt>필요 기술</dt><dd>{(project.skills || []).join(', ') || '-'}</dd></div>
        </dl>

        <div className="client-project-description">
          <h3>의뢰 내용</h3>
          <p>{project.description || '등록된 상세 의뢰 내용이 없습니다.'}</p>
        </div>

        {hasApplicants && (
          <div className="client-project-edit-actions">
            <p className="edit-disabled-guide">지원자가 있는 프로젝트는 수정할 수 없습니다.</p>
          </div>
        )}

        {editing && projectForm && (
          <form className="client-project-edit-form" onSubmit={updateProject}>
            <h3>프로젝트 수정</h3>
            <div className="form-grid">
              <label>
                프로젝트명
                <input name="title" value={projectForm.title} onChange={changeProjectForm} required />
              </label>
              <label>
                모집 마감일
                <input name="deadline" type="date" value={projectForm.deadline} onChange={changeProjectForm} required />
              </label>
              <label>
                계약 형태
                <select name="employmentType" value={projectForm.employmentType} onChange={changeProjectForm}>
                  <option value="OUTSOURCING">도급</option>
                  <option value="RESIDENT">상주</option>
                </select>
              </label>
              <label>
                예산(만원)
                <input name="budget" type="number" value={projectForm.budget} onChange={changeProjectForm} required />
              </label>
              <label>
                예상 기간(일)
                <input name="estimatedDuration" type="number" value={projectForm.estimatedDuration} onChange={changeProjectForm} />
              </label>
              <label>
                진행 방식
                <input name="workType" value={projectForm.workType} onChange={changeProjectForm} />
              </label>
            </div>
            <label>
              필요 기술
              <input name="requiredSkills" value={projectForm.requiredSkills} onChange={changeProjectForm} />
            </label>
            <label>
              업무 내용
              <textarea name="workContent" value={projectForm.workContent} onChange={changeProjectForm} rows={5} required />
            </label>
            <div className="client-project-edit-buttons">
              <button type="button" className="btn secondary" onClick={() => setEditing(false)}>취소</button>
              <button type="submit" className="btn primary" disabled={saving}>
                {saving ? '저장 중...' : '수정 저장'}
              </button>
            </div>
          </form>
        )}

        <div className="panel-title applicants-title">
          <div>
            <h2>지원자 리스트</h2>
            <p>지원자를 최근 지원 순으로 2명씩 확인할 수 있습니다.</p>
          </div>
        </div>

        {applicantError && <ErrorBox message={applicantError} />}
        {applicationLoading && <Loading message="지원서 상세를 불러오는 중입니다." />}
        {applicationError && <ErrorBox message={applicationError} />}

        {applicants.length === 0 ? (
          <div className="empty-box">지원자가 없습니다.</div>
        ) : (
          <div className="applicant-table-wrap">
            <table className="applicant-table">
              <thead>
                <tr>
                  <th scope="col">순번</th>
                  <th scope="col">지원자</th>
                  <th scope="col">예상 금액</th>
                  <th scope="col">지원일</th>
                  <th scope="col">상태</th>
                  <th scope="col">관리</th>
                </tr>
              </thead>
              <tbody>
                {applicants.map((application, index) => {
                  const applicationId = application.applicationId ?? application.id;
                  const accepted = application.status === 'ACCEPTED';
                  return (
                    <tr key={applicationId}>
                      <td>{index + 1}</td>
                      <td>
                        <strong>{application.developerName || '지원자'}</strong>
                        <span>{application.techCategory || '-'} · {application.experienceLevel || '-'}</span>
                      </td>
                      <td>{formatMoney(application.bidAmount, project.type)}</td>
                      <td>{formatDateTimeMinute(application.appliedAt)}</td>
                      <td>{formatApplicationStatus(application.status)}</td>
                      <td>
                        <div className="applicant-row-actions">
                          <button type="button" className="btn secondary" onClick={() => openApplication(application)}>
                            상세 보기
                          </button>
                          <button
                            type="button"
                            className={accepted ? 'btn completed-action' : 'btn primary'}
                            onClick={() => acceptApplication(application)}
                            disabled={accepted || acceptingId === applicationId}
                          >
                            {accepted ? '수락 완료' : acceptingId === applicationId ? '처리 중...' : '수락하기'}
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}

        {!applicantLast && (
          <button
            type="button"
            className="btn secondary full load-more"
            onClick={loadMoreApplicants}
            disabled={applicantLoading}
          >
            {applicantLoading ? '불러오는 중...' : '더보기'}
          </button>
        )}
      </section>

      {selectedApplication && (
        <div className="modal-backdrop" onClick={() => setSelectedApplication(null)}>
          <div className="modal" onClick={(event) => event.stopPropagation()}>
            <div className="modal-head">
              <div>
                <p className="eyebrow">Application Detail</p>
                <h3>지원서 상세</h3>
              </div>
              <button type="button" onClick={() => setSelectedApplication(null)} aria-label="지원서 상세 닫기">×</button>
            </div>
            <dl className="info-list">
              <div><dt>지원자</dt><dd>{selectedApplication.developerName || '-'}</dd></div>
              <div><dt>지원 금액</dt><dd>{formatMoney(selectedApplication.bidAmount, project.type)}</dd></div>
              <div><dt>지원일</dt><dd>{formatDateTimeMinute(selectedApplication.appliedAt)}</dd></div>
              <div><dt>지원 상태</dt><dd>{formatApplicationStatus(selectedApplication.status)}</dd></div>
              <div><dt>작업 기간</dt><dd>{formatExpectedPeriod(selectedApplication.workDuration)}</dd></div>
              <div><dt>기술 분야</dt><dd>{selectedApplication.techCategory || '-'}</dd></div>
              <div><dt>경력 수준</dt><dd>{selectedApplication.experienceLevel || '-'}</dd></div>
              <div><dt>투입 인원</dt><dd>{selectedApplication.headcount ? `${selectedApplication.headcount}명` : '-'}</dd></div>
            </dl>
            <h4>지원서 내용</h4>
            <div className="application-content">
              {selectedApplication.proposalContent
                || selectedApplication.proposalSummary
                || '작성된 지원서 내용이 없습니다.'}
            </div>
          </div>
        </div>
      )}
    </section>
  );
}
