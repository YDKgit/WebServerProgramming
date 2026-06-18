import { useEffect, useMemo, useState } from 'react';
import { developerApi } from '../../api/api';
import ErrorBox from '../common/ErrorBox.jsx';
import Loading from '../common/Loading.jsx';
import TagInput from '../common/TagInput.jsx';
import { getImageUrl, splitCsv } from '../../utils/format';

const emptyProfile = {
  name: '',
  profileImage: '',
  supportFields: '',
  introduction: '',
  isAvailable: true,
  isOnsiteAvailable: false,
  regionMain: '',
  regionSub: '',
  businessType: '',
  careerYear: '',
  tags: [],
};

const SUPPORT_FIELD_OPTIONS = ['개발', '백엔드', '프론트엔드', '풀스택', '디자인', '기획'];
const REGION_OPTIONS = ['서울', '경기', '인천', '부산', '대구', '대전', '광주', '울산', '세종', '강원', '충북', '충남', '전북', '전남', '경북', '경남', '제주'];
const BUSINESS_TYPE_OPTIONS = ['개인프리랜서', '팀프리랜서', '개인사업자', '법인사업자'];
const CAREER_OPTIONS = Array.from({ length: 16 }, (_, index) => index === 15 ? '15년 이상' : `${index + 1}년`);

function toProfileView(profile = {}) {
  return {
    ...emptyProfile,
    ...profile,
    tags: splitCsv(profile.tags || profile.searchTags),
    profileImage: profile.profileImage || profile.imageUrl || '',
  };
}

export default function ProfileEditor() {
  const [profile, setProfile] = useState(emptyProfile);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [imageLoadFailed, setImageLoadFailed] = useState(false);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  const profileImageUrl = getImageUrl(profile.profileImage);
  const profileInitial = profile.name?.trim()?.slice(0, 1) || 'P';
  const selectedSupportFields = useMemo(
    () => splitCsv(profile.supportFields),
    [profile.supportFields],
  );
  const supportFieldOptions = useMemo(
    () => [...new Set([...SUPPORT_FIELD_OPTIONS, ...selectedSupportFields])],
    [selectedSupportFields],
  );

  useEffect(() => {
    async function loadProfile() {
      setLoading(true);
      setError('');
      try {
        setProfile(toProfileView(await developerApi.getProfile()));
      } catch (event) {
        setError(event.message);
      } finally {
        setLoading(false);
      }
    }
    loadProfile();
  }, []);

  const handleChange = (event) => {
    const { name, value, type, checked } = event.target;
    setProfile((prev) => ({ ...prev, [name]: type === 'checkbox' ? checked : value }));
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
      await developerApi.uploadProfileImage(file);
      const refreshed = await developerApi.getProfile();
      setProfile(toProfileView(refreshed));
      setImageLoadFailed(false);
      setMessage('프로필 이미지가 업로드되었습니다.');
    } catch (event) {
      setError(event.message);
    } finally {
      setUploading(false);
      event.target.value = '';
    }
  };

  const toggleSupportField = (field) => {
    const nextFields = selectedSupportFields.includes(field)
      ? selectedSupportFields.filter((item) => item !== field)
      : [...selectedSupportFields, field];
    setProfile((prev) => ({ ...prev, supportFields: nextFields.join(',') }));
  };

  const submitProfile = async (event) => {
    event.preventDefault();
    if ((profile.tags || []).length > 5) {
      setError('검색태그는 5개를 초과해 등록할 수 없습니다.');
      return;
    }
    setSaving(true);
    setError('');
    setMessage('');
    try {
      const updated = await developerApi.updateProfile({
        supportFields: profile.supportFields,
        searchTags: (profile.tags || []).join(','),
        introduction: profile.introduction,
        isAvailable: profile.isAvailable,
        isOnsiteAvailable: profile.isOnsiteAvailable,
        regionMain: profile.regionMain,
        regionSub: profile.regionSub,
        businessType: profile.businessType,
        careerYear: profile.careerYear,
      });
      setProfile(toProfileView(updated));
      setMessage('프로필이 수정되었습니다.');
    } catch (event) {
      setError(event.message);
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <Loading message="프로필을 불러오는 중입니다." />;

  return (
    <form className="panel profile-form-card" onSubmit={submitProfile}>
      <div className="profile-form-heading">
        <div>
          <p className="eyebrow">Developer Profile</p>
          <h2>프로필 수정</h2>
          <p>프로젝트 담당자가 확인할 기본 정보와 전문 분야를 관리해 주세요.</p>
        </div>
        <span className="profile-required-guide"><b>*</b> 필수 입력 정보</span>
      </div>

      <ErrorBox message={error} />
      {message && <div className="success-box">{message}</div>}

      <div className="profile-form-row profile-photo-row">
        <div className="profile-field-label">프로필 이미지</div>
        <div className="profile-photo-content">
          <div className="profile-image-wrap">
            {profileImageUrl && !imageLoadFailed ? (
              <img
                src={profileImageUrl}
                alt={`${profile.name || '사용자'} 프로필`}
                onError={() => setImageLoadFailed(true)}
              />
            ) : (
              <div className="profile-placeholder" aria-label="기본 프로필 이미지">
                <span>{profileInitial}</span>
              </div>
            )}
          </div>
          <div className="profile-photo-actions">
            <strong>{profile.name || '개발자'}</strong>
            <label className="btn profile-upload-button file-button">
              {uploading ? '업로드 중...' : '업데이트'}
              <input
                type="file"
                accept="image/*"
                onChange={handleImageChange}
                disabled={uploading}
              />
            </label>
            <p className="help-text">JPG, PNG, GIF, WEBP 형식의 5MB 이하 이미지를 등록해 주세요.</p>
          </div>
        </div>
      </div>

      <div className="profile-form-row">
        <div className="profile-field-label required">지원분야</div>
        <div className="profile-checkbox-group">
          {supportFieldOptions.map((field) => (
            <label className="profile-check-option" key={field}>
              <input
                type="checkbox"
                checked={selectedSupportFields.includes(field)}
                onChange={() => toggleSupportField(field)}
              />
              <span>{field}</span>
            </label>
          ))}
        </div>
      </div>

      <div className="profile-form-row">
        <div className="profile-field-label">활동가능여부</div>
        <div>
          <label className="profile-toggle-option">
          <input name="isAvailable" type="checkbox" checked={Boolean(profile.isAvailable)} onChange={handleChange} />
            <span>현재 프로젝트 활동이 가능합니다.</span>
          </label>
          <p className="profile-field-help">활동 가능으로 설정하면 프로젝트 추천을 받을 수 있습니다.</p>
        </div>
      </div>

      <div className="profile-form-row">
        <div className="profile-field-label">상주가능여부</div>
        <div>
          <label className="profile-toggle-option">
          <input name="isOnsiteAvailable" type="checkbox" checked={Boolean(profile.isOnsiteAvailable)} onChange={handleChange} />
            <span>고객사 또는 지정 장소에 상주할 수 있습니다.</span>
          </label>
          <p className="profile-field-help">상주 가능 여부에 따라 적합한 프로젝트가 추천됩니다.</p>
        </div>
      </div>

      <div className="profile-form-row">
        <div className="profile-field-label required">지역</div>
        <div className="profile-inline-fields">
          <select name="regionMain" value={profile.regionMain || ''} onChange={handleChange}>
            <option value="">시/도 선택</option>
            {!REGION_OPTIONS.includes(profile.regionMain) && profile.regionMain && (
              <option value={profile.regionMain}>{profile.regionMain}</option>
            )}
            {REGION_OPTIONS.map((region) => <option value={region} key={region}>{region}</option>)}
          </select>
          <input
            name="regionSub"
            value={profile.regionSub || ''}
            onChange={handleChange}
            placeholder="시/군/구 입력"
          />
        </div>
      </div>

      <div className="profile-form-row">
        <div className="profile-field-label required">형태</div>
        <div className="profile-inline-fields">
          <select name="businessType" value={profile.businessType || ''} onChange={handleChange}>
            <option value="">활동 형태 선택</option>
            {!BUSINESS_TYPE_OPTIONS.includes(profile.businessType) && profile.businessType && (
              <option value={profile.businessType}>{profile.businessType}</option>
            )}
            {BUSINESS_TYPE_OPTIONS.map((type) => <option value={type} key={type}>{type}</option>)}
          </select>
          <select name="careerYear" value={profile.careerYear || ''} onChange={handleChange}>
            <option value="">경력 선택</option>
            {!CAREER_OPTIONS.includes(profile.careerYear) && profile.careerYear && (
              <option value={profile.careerYear}>{profile.careerYear}</option>
            )}
            {CAREER_OPTIONS.map((career) => <option value={career} key={career}>{career}</option>)}
          </select>
        </div>
      </div>

      <div className="profile-form-row">
        <div className="profile-field-label">검색태그</div>
        <TagInput
          value={profile.tags || []}
          onChange={(tags) => setProfile((prev) => ({ ...prev, tags }))}
          max={5}
          placeholder="예: Spring Boot"
        />
      </div>

      <div className="profile-form-row profile-introduction-row">
        <div className="profile-field-label required">소개글</div>
        <div>
          <textarea
            name="introduction"
            value={profile.introduction || ''}
            onChange={handleChange}
            rows={7}
            maxLength={1000}
            placeholder="주요 경력, 강점, 프로젝트 경험을 소개해 주세요."
          />
          <p className="profile-character-count">{(profile.introduction || '').length} / 1000</p>
        </div>
      </div>

      <div className="profile-form-actions">
        <button type="submit" className="btn profile-save-button" disabled={saving || uploading}>
          {saving ? '저장 중...' : '저장하기'}
        </button>
      </div>
    </form>
  );
}
