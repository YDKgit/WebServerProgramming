import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { authApi } from '../../api/api';
import ErrorBox from '../common/ErrorBox.jsx';

export default function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const [form, setForm] = useState({ loginId: 'dev01', password: '1234' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const from = location.state?.from?.pathname || '/projects';

  const changeForm = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const submitLogin = async (event) => {
    event.preventDefault();
    setError('');
    setLoading(true);

    try {
      const user = await authApi.login(form);
      localStorage.setItem('loginUser', JSON.stringify(user));
      navigate(from, { replace: true });
    } catch (event) {
      setError(event.message || '로그인에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="login-page">
      <div className="login-card">
        <Link to="/projects" className="brand login-brand">
          <img className="brand-logo" src="/freemoa-logo-none.svg" alt="Freemoa" />
        </Link>
        <p className="eyebrow">IT 프로젝트 매칭</p>
        <h1>프로젝트와 파트너를 빠르게 연결합니다</h1>
        <p className="subtitle">
          dev01 또는 client01 계정으로 로그인한 뒤 프로젝트 찾기, 지원, 의뢰인/개발자 마이페이지를 확인할 수 있습니다.
        </p>

        <div className="quick-login-row">
          <button type="button" className="secondary-button" onClick={() => setForm({ loginId: 'dev01', password: '1234' })}>
            개발자 계정
          </button>
          <button type="button" className="secondary-button" onClick={() => setForm({ loginId: 'client01', password: '1234' })}>
            의뢰인 계정
          </button>
        </div>

        <form className="login-form" onSubmit={submitLogin}>
          <label>
            아이디
            <input
              name="loginId"
              value={form.loginId}
              onChange={changeForm}
              placeholder="dev01"
              autoComplete="username"
              required
            />
          </label>

          <label>
            비밀번호
            <input
              name="password"
              type="password"
              value={form.password}
              onChange={changeForm}
              placeholder="1234"
              autoComplete="current-password"
              required
            />
          </label>

          <ErrorBox message={error} />

          <button type="submit" className="primary-button full-width" disabled={loading}>
            {loading ? '로그인 중...' : '로그인'}
          </button>
        </form>
      </div>
    </section>
  );
}
