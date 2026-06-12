import { NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { authApi } from '../../api/api';

export default function Layout() {
  const navigate = useNavigate();
  const location = useLocation();
  const loginUser = JSON.parse(localStorage.getItem('loginUser') || 'null');
  const isDeveloper = loginUser?.role === 'DEVELOPER';
  const isClient = loginUser?.role === 'CLIENT';
  const clientTab = new URLSearchParams(location.search).get('tab');
  const requestMenuActive = location.pathname === '/client/mypage' && clientTab === 'request';
  const managementMenuActive = location.pathname.startsWith('/client/projects')
    || (location.pathname === '/client/mypage' && clientTab !== 'request');

  const logout = async () => {
    try {
      await authApi.logout();
    } catch (event) {
      console.error('로그아웃 요청 실패:', event);
    } finally {
      localStorage.removeItem('loginUser');
      navigate('/login', { replace: true });
    }
  };

  return (
    <div className="app-shell">
      <header className="top-header">
        <div className="header-inner">
          <NavLink to="/projects" className="brand" aria-label="Freemoa">
            <img className="brand-logo" src="/freemoa-logo-none.svg" alt="Freemoa" />
          </NavLink>

          <nav className="main-nav" aria-label="주요 메뉴">
            <NavLink to="/projects">프로젝트</NavLink>
            {isDeveloper && <NavLink to="/developer/mypage">파트너스</NavLink>}
            {isClient && (
              <>
                <NavLink
                  to="/client/mypage?tab=request"
                  className={() => (requestMenuActive ? 'active' : undefined)}
                >
                  무료 견적 의뢰
                </NavLink>
                <NavLink
                  to="/client/mypage?tab=projects"
                  className={() => (managementMenuActive ? 'active' : undefined)}
                >
                  의뢰인 관리
                </NavLink>
              </>
            )}
          </nav>

          <div className="header-user-box">
            {loginUser ? (
              <>
                <span>{loginUser.name || '회원'}님</span>
                <button type="button" onClick={logout}>로그아웃</button>
              </>
            ) : (
              <NavLink to="/login" className="login-link">로그인</NavLink>
            )}
          </div>
        </div>
      </header>

      <main className="page-container">
        <Outlet />
      </main>
    </div>
  );
}
