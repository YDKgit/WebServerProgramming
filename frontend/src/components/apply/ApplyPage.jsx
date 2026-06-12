import { useEffect, useMemo, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { developerApi, projectApi } from '../../api/api';
import ErrorBox from '../common/ErrorBox.jsx';
import Loading from '../common/Loading.jsx';
import { formatMoney, formatType, hasContactInfo, isProjectClosed, toProjectView } from '../../utils/format';

const initialResidentForm = {
  workPeriod: '',
  bidAmount: '',
  content: '',
};

const initialOutsourcingForm = {
  skillCategory: '개발',
  careerYear: '',
  headcount: 1,
  bidAmount: '',
  content: '',
};

export default function ApplyPage() {
  const loginUser = JSON.parse(localStorage.getItem('loginUser') || 'null');
  const { projectId } = useParams();
  const navigate = useNavigate();
  const [project, setProject] = useState(null);
  const [form, setForm] = useState(initialResidentForm);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [fieldError, setFieldError] = useState('');
  const [eligibilityError, setEligibilityError] = useState('');

  useEffect(() => {
    async function loadProject() {
      setLoading(true);
      setError('');
      setEligibilityError('');
      if (loginUser?.role !== 'DEVELOPER') {
        setEligibilityError('개발자만 프로젝트에 지원할 수 있습니다.');
        setLoading(false);
        return;
      }
      try {
        const [projectResult, applications] = await Promise.all([
          projectApi.getProject(projectId),
          developerApi.getAppliedProjects(),
        ]);
        const result = toProjectView(projectResult);
        setProject(result);
        setForm(result.type === 'RESIDENT' ? initialResidentForm : initialOutsourcingForm);
        if (isProjectClosed(result)) {
          setEligibilityError('마감된 프로젝트에는 지원할 수 없습니다.');
        } else if (applications.some((application) => Number(application.projectId) === Number(projectId))) {
          setEligibilityError('이미 지원한 프로젝트입니다.');
        }
      } catch (event) {
        setError(event.message);
      } finally {
        setLoading(false);
      }
    }
    loadProject();
  }, [projectId, loginUser?.role]);

  const isResident = project?.type === 'RESIDENT';

  const canSubmit = useMemo(() => {
    if (!project || eligibilityError) return false;
    if (!form.bidAmount || !form.content.trim()) return false;
    if (hasContactInfo(form.content)) return false;
    if (isResident) return Boolean(form.workPeriod);
    return Boolean(form.skillCategory && form.careerYear);
  }, [eligibilityError, form, isResident, project]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    if (name === 'content' && hasContactInfo(value)) {
      setFieldError('지원 내용에는 이메일 또는 전화번호를 입력할 수 없습니다.');
    } else {
      setFieldError('');
    }
  };

  const submitApplication = async (event) => {
    event.preventDefault();
    if (hasContactInfo(form.content)) {
      setFieldError('지원 내용에는 이메일 또는 전화번호를 입력할 수 없습니다.');
      return;
    }
    setSaving(true);
    setError('');
    try {
      await developerApi.applyProject(project.id, {
        workDuration: Number(form.workPeriod) || null,
        bidAmount: Number(form.bidAmount),
        proposalContent: form.content,
        techCategory: isResident ? null : form.skillCategory,
        experienceLevel: isResident ? null : form.careerYear,
        headcount: isResident ? 1 : Number(form.headcount || 1),
      });
      alert('지원서가 제출되었습니다.');
      navigate('/developer/mypage');
    } catch (event) {
      setError(event.message);
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <Loading message="지원할 프로젝트 정보를 불러오는 중입니다." />;
  if (eligibilityError && !project) return <ErrorBox message={eligibilityError} />;
  if (error && !project) return <ErrorBox message={error} />;
  if (!project) return <ErrorBox message="프로젝트를 찾을 수 없습니다." />;

  return (
    <section>
      <div className="page-title-row">
        <div>
          <p className="eyebrow">Application</p>
          <h1>프로젝트 지원하기</h1>
          <p className="subtitle">지원서에는 직접 연락처를 남길 수 없습니다.</p>
        </div>
        <Link to={`/projects/${project.id}`} className="btn secondary">상세로 돌아가기</Link>
      </div>

      <div className="two-column apply-layout">
        <section className="panel sticky-panel">
          <h2>프로젝트 요약</h2>
          <dl className="info-list">
            <div><dt>프로젝트명</dt><dd>{project.title}</dd></div>
            <div><dt>계약 형태</dt><dd>{formatType(project.type)}</dd></div>
            <div><dt>기준 금액</dt><dd>{formatMoney(project.budget, project.type)}</dd></div>
            <div><dt>필요 기술</dt><dd>{(project.skills || []).join(', ') || '-'}</dd></div>
          </dl>
        </section>

        <form className="panel form-panel" onSubmit={submitApplication}>
          <h2>{isResident ? '상주 지원서 입력' : '도급 지원서 입력'}</h2>
          <ErrorBox message={error} />
          <ErrorBox message={eligibilityError} />

          {isResident ? (
            <>
              <label>작업 기간(일)
                <input name="workPeriod" type="number" value={form.workPeriod} onChange={handleChange} placeholder="예: 90" required />
              </label>
              <label>지원 금액(만원)
                <input name="bidAmount" type="number" value={form.bidAmount} onChange={handleChange} placeholder="예: 700" required />
              </label>
            </>
          ) : (
            <>
              <label>기술 구분
                <select name="skillCategory" value={form.skillCategory} onChange={handleChange}>
                  <option value="개발">개발</option>
                  <option value="디자인">디자인</option>
                  <option value="기획">기획</option>
                </select>
              </label>
              <label>연차 구분
                <input name="careerYear" value={form.careerYear} onChange={handleChange} placeholder="예: 중급" required />
              </label>
              <label>투입 인원
                <input name="headcount" type="number" min="1" value={form.headcount} onChange={handleChange} required />
              </label>
              <label>견적 금액(만원)
                <input name="bidAmount" type="number" value={form.bidAmount} onChange={handleChange} placeholder="예: 800" required />
              </label>
            </>
          )}

          <label>지원 내용
            <textarea
              name="content"
              value={form.content}
              onChange={handleChange}
              placeholder="경험, 수행 방식, 예상 일정 등을 입력하세요. 이메일과 전화번호는 입력할 수 없습니다."
              rows={8}
              required
            />
          </label>
          {fieldError && <p className="field-error">{fieldError}</p>}

          <button type="submit" className="btn primary full" disabled={!canSubmit || saving || Boolean(eligibilityError)}>
            {eligibilityError ? '지원 불가' : saving ? '제출 중...' : '지원서 제출'}
          </button>
        </form>
      </div>
    </section>
  );
}
