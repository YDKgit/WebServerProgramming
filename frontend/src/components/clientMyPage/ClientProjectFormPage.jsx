import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { projectApi } from '../../api/api';
import ErrorBox from '../common/ErrorBox.jsx';
import TagInput from '../common/TagInput.jsx';

const initialForm = {
  title: '',
  deadline: '',
  type: 'OUTSOURCING',
  budget: '',
  expectedPeriod: '',
  description: '',
  workMethod: '원격',
  skills: [],
};

export default function ClientProjectFormPage({ embedded = false, onCreated }) {
  const navigate = useNavigate();
  const [form, setForm] = useState(initialForm);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const submitProject = async (event) => {
    event.preventDefault();
    setSaving(true);
    setError('');
    try {
      const createdProject = await projectApi.createProject({
        title: form.title,
        deadline: form.deadline,
        employmentType: form.type,
        budget: Number(form.budget),
        workContent: form.description,
        requiredSkills: form.skills.join(', '),
        estimatedDuration: Number(form.expectedPeriod) || null,
        workType: form.workMethod,
      });
      alert('프로젝트가 등록되었습니다.');
      setForm(initialForm);
      if (onCreated) {
        await onCreated(createdProject);
      } else {
        navigate('/client/mypage?tab=projects');
      }
    } catch (event) {
      setError(event.message);
    } finally {
      setSaving(false);
    }
  };

  return (
    <section className={embedded ? 'embedded-project-form' : ''}>
      {!embedded && (
        <div className="page-title-row">
          <div>
            <p className="eyebrow">Client Request</p>
            <h1>프로젝트 의뢰하기</h1>
            <p className="subtitle">파트너가 제안할 수 있도록 프로젝트 기본 정보를 정리해 주세요.</p>
          </div>
        </div>
      )}

      <form className="panel form-panel wide-form" onSubmit={submitProject}>
        <div className="panel-title">
          <div>
            <h2>기본 정보</h2>
            <p>제목, 마감일, 예산은 프로젝트 카드에 노출됩니다.</p>
          </div>
        </div>
        <ErrorBox message={error} />

        <div className="form-grid">
          <label>프로젝트명
            <input name="title" value={form.title} onChange={handleChange} required />
          </label>
          <label>모집 마감일
            <input name="deadline" type="date" value={form.deadline} onChange={handleChange} required />
          </label>
          <label>계약 형태
            <select name="type" value={form.type} onChange={handleChange}>
              <option value="OUTSOURCING">도급</option>
              <option value="RESIDENT">상주</option>
            </select>
          </label>
          <label>{form.type === 'RESIDENT' ? '월 급여(만원)' : '예상 예산(만원)'}
            <input name="budget" type="number" value={form.budget} onChange={handleChange} required />
          </label>
          <label>예상 기간(일)
            <input name="expectedPeriod" type="number" value={form.expectedPeriod} onChange={handleChange} placeholder="예: 90" />
          </label>
          <label>진행 방식
            <input name="workMethod" value={form.workMethod} onChange={handleChange} placeholder="예: 원격 / 상주" />
          </label>
        </div>

        <label>업무 내용
          <textarea name="description" value={form.description} onChange={handleChange} rows={6} required />
        </label>

        <div className="form-section">
          <label>필요 기술 스택</label>
          <TagInput value={form.skills} onChange={(skills) => setForm((prev) => ({ ...prev, skills }))} placeholder="예: React" />
        </div>

        <button type="submit" className="btn primary full" disabled={saving}>
          {saving ? '등록 중...' : '프로젝트 등록'}
        </button>
      </form>
    </section>
  );
}
