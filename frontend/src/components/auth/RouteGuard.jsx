import { Navigate, Outlet, useLocation } from 'react-router-dom';

function getLoginUser() {
  try {
    return JSON.parse(localStorage.getItem('loginUser') || 'null');
  } catch {
    localStorage.removeItem('loginUser');
    return null;
  }
}

function getRoleHome(role) {
  if (role === 'CLIENT') return '/client/mypage?tab=projects';
  if (role === 'DEVELOPER') return '/developer/mypage';
  return '/projects';
}

export function RequireAuth() {
  const location = useLocation();
  const loginUser = getLoginUser();

  if (!loginUser) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  return <Outlet />;
}

export function RequireRole({ role, children }) {
  const loginUser = getLoginUser();

  if (!loginUser) {
    return <Navigate to="/login" replace />;
  }

  if (loginUser.role !== role) {
    return <Navigate to={getRoleHome(loginUser.role)} replace />;
  }

  return children;
}
