import { useEffect, useState } from 'react';
import { memberApi } from '../../api/api';
import { getImageUrl } from '../../utils/format';
import ErrorBox from '../common/ErrorBox.jsx';
import Loading from '../common/Loading.jsx';

const emptyProfile = {
  name: '',
  profileImage: '',
  introduction: '',
  regionMain: '',
  regionSub: '',
  businessType: '',
};

const REGION_OPTIONS = [
  '서울', '경기', '인천', '부산', '대구', '대전', '광주', '울산',
  '세종', '강원', '충북', '충남', '전북', '전남', '경북', '경남', '제주',
];

const BUSINESS_TYPE_OPTIONS = ['개인', '개인사업자', '법인사업자', '법인'];

export default function ClientProfilePanel() {
  const [profile, setProfile] = useState(emptyProfile);
  const [imageLoadFailed, setImageLoadFailed] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  useEffect(() => {
    let ignore = false;

    async function loadProfile() {
      setLoading(true);
      setError('');
      try {
        const result = await memberApi.getProfile();
        if (!ignore) {
          setProfile({ ...emptyProfile, ...result });
          setImageLoadFailed(false);
        }
      } catch (event) {
        console.error('의뢰인 프로필 조회 실패:', event);
        if (!ignore) {
          setError(event.message || '프로필 정보를 불러오지 못했습니다.');
        }
      } finally {
        if (!ignore) setLoading(false);
      }
    }

    loadProfile();
    return () => {
      ignore = true;
    };
  }, []);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setProfile((current) => ({ ...current, [name]: value }));
  };

  const updateLoginUserName = (name) => {
    const loginUser = JSON.parse(localStorage.getItem('loginUser') || 'null');
    if (!loginUser) return;

    const updatedLoginUser = { ...loginUser, name };
    localStorage.setItem('loginUser', JSON.stringify(updatedLoginUser));
    window.dispatchEvent(new CustomEvent('login-user-updated', {
      detail: updatedLoginUser,
    }));
  };

  const handleImageChange = async (event) => {
    const file = event.target.files?.[0];
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      setError('이미지 파일만 선택할 수 있습니다.');
      event.target.value = '';
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      setError('프로필 이미지는 5MB 이하만 업로드할 수 있습니다.');
      event.target.value = '';
      return;
    }

    setUploading(true);
    setError('');
    setMessage('');
    try {
      const result = await memberApi.uploadProfileImage(file);
      setProfile((current) => ({
        ...current,
        profileImage: result.profileImage || result.imageUrl || '',
      }));
      setImageLoadFailed(false);
      setMessage('프로필 이미지가 변경되었습니다.');
    } catch (event) {
      setError(event.message || '프로필 이미지를 업로드하지 못했습니다.');
    } finally {
      setUploading(false);
      event.target.value = '';
    }
  };

  const submitProfile = async (event) => {
    event.preventDefault();
    if (!profile.name.trim()) {
      setError('이름을 입력해 주세요.');
      return;
    }

    setSaving(true);
    setError('');
    setMessage('');
    try {
      const updated = await memberApi.updateProfile({
        name: profile.name.trim(),
        introduction: profile.introduction,
        regionMain: profile.regionMain,
        regionSub: profile.regionSub,
        businessType: profile.businessType,
      });
      setProfile({ ...emptyProfile, ...updated });
      updateLoginUserName(updated.name);
      setMessage('프로필 정보가 저장되었습니다.');
    } catch (event) {
      setError(event.message || '프로필 정보를 저장하지 못했습니다.');
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <Loading message="프로필 정보를 불러오는 중입니다." />;

  const imageUrl = getImageUrl(profile.profileImage);
  const initial = profile.name?.trim()?.slice(0, 1) || 'C';

  return (
    <form className="panel client-profile-panel client-profile-form" onSubmit={submitProfile}>
      <div className="panel-title">
        <div>
          <h2>의뢰인 프로필 수정</h2>
          <p>프로젝트 의뢰 시 사용되는 기본 회원 정보를 관리합니다.</p>
        </div>
      </div>

      <ErrorBox message={error} />
      {message && <div className="success-box">{message}</div>}

      <div className="client-profile-head">
        <div className="client-profile-image">
          {imageUrl && !imageLoadFailed ? (
            <img
              src={imageUrl}
              alt={`${profile.name || '의뢰인'} 프로필`}
              onError={() => setImageLoadFailed(true)}
            />
          ) : (
            <span>{initial}</span>
          )}
        </div>
        <div className="client-profile-image-actions">
          <span className="badge primary">CLIENT</span>
          <strong>{profile.name || '의뢰인'}</strong>
          <label className="btn secondary file-button">
            {uploading ? '업로드 중...' : '이미지 변경'}
            <input
              type="file"
              accept="image/*"
              onChange={handleImageChange}
              disabled={uploading}
            />
          </label>
          <p>JPG, PNG, GIF, WEBP 형식의 5MB 이하 이미지를 등록할 수 있습니다.</p>
        </div>
      </div>

      <div className="client-profile-edit-grid">
        <label>
          이름
          <input
            name="name"
            value={profile.name}
            onChange={handleChange}
            maxLength={50}
            required
          />
        </label>

        <label>
          사업자 유형
          <select name="businessType" value={profile.businessType || ''} onChange={handleChange}>
            <option value="">사업자 유형 선택</option>
            {!BUSINESS_TYPE_OPTIONS.includes(profile.businessType) && profile.businessType && (
              <option value={profile.businessType}>{profile.businessType}</option>
            )}
            {BUSINESS_TYPE_OPTIONS.map((type) => (
              <option value={type} key={type}>{type}</option>
            ))}
          </select>
        </label>

        <label>
          지역
          <select name="regionMain" value={profile.regionMain || ''} onChange={handleChange}>
            <option value="">지역 선택</option>
            {!REGION_OPTIONS.includes(profile.regionMain) && profile.regionMain && (
              <option value={profile.regionMain}>{profile.regionMain}</option>
            )}
            {REGION_OPTIONS.map((region) => (
              <option value={region} key={region}>{region}</option>
            ))}
          </select>
        </label>

        <label>
          상세 지역
          <input
            name="regionSub"
            value={profile.regionSub || ''}
            onChange={handleChange}
            placeholder="예: 서초구"
          />
        </label>
      </div>

      <label className="client-profile-introduction-field">
        소개
        <textarea
          name="introduction"
          value={profile.introduction || ''}
          onChange={handleChange}
          rows={6}
          maxLength={1000}
          placeholder="회사 또는 프로젝트 의뢰 목적을 소개해 주세요."
        />
        <span>{(profile.introduction || '').length} / 1000</span>
      </label>

      <div className="client-profile-save-actions">
        <button type="submit" className="btn primary" disabled={saving || uploading}>
          {saving ? '저장 중...' : '저장하기'}
        </button>
      </div>
    </form>
  );
}
