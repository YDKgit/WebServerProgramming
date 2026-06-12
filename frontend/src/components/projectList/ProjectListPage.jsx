import { useEffect, useState } from 'react';
import ErrorBox from '../common/ErrorBox.jsx';
import Loading from '../common/Loading.jsx';
import ProjectCard from './ProjectCard.jsx';
import { developerApi, projectApi } from '../../api/api';

const PAGE_SIZE = 4;

const employmentTypeOptions = [
  { value: 'ALL', label: '전체' },
  { value: 'OUTSOURCING', label: '도급' },
  { value: 'RESIDENT', label: '상주' },
];

const sortOptions = [
  { value: 'latest', label: '최신순' },
  { value: 'deadline', label: '마감 임박순' },
  { value: 'budgetDesc', label: '금액 높은순' },
  { value: 'applicantsDesc', label: '지원자 많은순' },
];

const statusOptions = [
  { value: 'ALL', label: '전체' },
  { value: 'RECRUITING', label: '모집중' },
  { value: 'CLOSED', label: '마감' },
];

export default function ProjectListPage() {
  const loginUser = JSON.parse(localStorage.getItem('loginUser') || 'null');
  const [employmentType, setEmploymentType] = useState('ALL');
  const [sort, setSort] = useState('latest');
  const [status, setStatus] = useState('ALL');
  const [page, setPage] = useState(0);
  const [appliedProjectIds, setAppliedProjectIds] = useState(new Set());
  const [appliedReady, setAppliedReady] = useState(false);
  const [data, setData] = useState({ content: [], totalPages: 0, totalElements: 0 });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    let ignore = false;

    async function loadProjects() {
      setLoading(true);
      setError('');
      try {
        const result = await projectApi.getProjects({
          employmentType,
          status,
          sort,
          page,
          size: PAGE_SIZE,
        });
        if (!ignore) {
          setData({
            content: result.content || [],
            totalPages: result.totalPages || 0,
            totalElements: result.totalElements || 0,
          });
        }
      } catch (event) {
        if (!ignore) setError(event.message);
      } finally {
        if (!ignore) setLoading(false);
      }
    }

    loadProjects();
    return () => {
      ignore = true;
    };
  }, [employmentType, status, sort, page]);

  useEffect(() => {
    if (loginUser?.role !== 'DEVELOPER') {
      setAppliedProjectIds(new Set());
      setAppliedReady(true);
      return;
    }

    setAppliedReady(false);
    developerApi.getAppliedProjects()
      .then((applications) => {
        setAppliedProjectIds(new Set(applications.map((application) => Number(application.projectId))));
      })
      .catch((event) => {
        console.error('지원 완료 프로젝트 조회 실패:', event);
      })
      .finally(() => {
        setAppliedReady(true);
      });
  }, [loginUser?.role]);

  const changeEmploymentType = (event) => {
    setEmploymentType(event.target.value);
    setPage(0);
  };

  const changeSort = (event) => {
    setSort(event.target.value);
    setPage(0);
  };

  const changeStatus = (event) => {
    setStatus(event.target.value);
    setPage(0);
  };

  return (
    <section className="project-finder-page">
      <div className="finder-hero">
        <p className="eyebrow">Project Matching</p>
        <h1>검증된 IT 프로젝트를 찾아보세요</h1>
        <p className="subtitle">
          프로젝트 유형과 정렬 조건을 바꿀 때마다 서버에서 새 목록을 불러옵니다.
        </p>
      </div>

      <div className="finder-layout">
        <aside className="filter-sidebar">
          <div className="filter-section">
            <h2>프로젝트 찾기</h2>
            <p>현재 페이지당 4개씩 표시됩니다.</p>
          </div>

          <label>
            프로젝트 형태
            <select value={employmentType} onChange={changeEmploymentType}>
              {employmentTypeOptions.map((option) => (
                <option value={option.value} key={option.value}>{option.label}</option>
              ))}
            </select>
          </label>

          <label>
            정렬
            <select value={sort} onChange={changeSort}>
              {sortOptions.map((option) => (
                <option value={option.value} key={option.value}>{option.label}</option>
              ))}
            </select>
          </label>

          <label>
            모집 상태
            <select value={status} onChange={changeStatus}>
              {statusOptions.map((option) => (
                <option value={option.value} key={option.value}>{option.label}</option>
              ))}
            </select>
          </label>
        </aside>

        <div className="finder-results">
          <div className="result-toolbar">
            <div>
              <strong>총 {data.totalElements.toLocaleString()}개 프로젝트</strong>
              <span>
                {employmentTypeOptions.find((option) => option.value === employmentType)?.label}
                {' / '}
                {statusOptions.find((option) => option.value === status)?.label}
                {' / '}
                {sortOptions.find((option) => option.value === sort)?.label}
              </span>
            </div>
          </div>

          <ErrorBox message={error} />
          {loading && <Loading message="프로젝트를 불러오는 중입니다." />}

          {!loading && !error && (
            <>
              {data.content.length === 0 ? (
                <div className="empty-box">조건에 맞는 프로젝트가 없습니다.</div>
              ) : (
                <div className="project-list">
                  {data.content.map((project) => (
                    <ProjectCard
                      project={project}
                      loginUser={loginUser}
                      applied={appliedProjectIds.has(Number(project.id))}
                      appliedReady={appliedReady}
                      key={project.id}
                    />
                  ))}
                </div>
              )}

              {data.totalPages > 1 && (
                <div className="pagination">
                  {Array.from({ length: data.totalPages }, (_, index) => index).map((pageNumber) => (
                    <button
                      className={pageNumber === page ? 'page active' : 'page'}
                      key={pageNumber}
                      type="button"
                      onClick={() => setPage(pageNumber)}
                    >
                      {pageNumber + 1}
                    </button>
                  ))}
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </section>
  );
}
