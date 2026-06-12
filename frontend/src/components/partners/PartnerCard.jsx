export default function PartnerCard({
  partner,
  loginUser,
  favorite,
  onToggleFavorite,
}) {
  const requestQuote = () => {
    window.alert(`${partner.maskedName} 파트너에게 견적 요청 기능은 추후 API와 연결할 수 있습니다.`);
  };

  const openVideo = () => {
    window.alert(`${partner.maskedName} 파트너의 영상 인터뷰 기능은 준비 중입니다.`);
  };

  return (
    <article className="partner-card">
      <div className="partner-card-main">
        <div className="partner-profile-heading">
          <div className="partner-avatar" style={{ background: partner.avatarColor }}>
            {partner.name.slice(0, 1)}
          </div>
          <div>
            <h2>{partner.maskedName}</h2>
            <p>{partner.businessType} · {partner.region}</p>
            <div className="partner-field-tags">
              {partner.fields.map((field) => <span key={field}>{field}</span>)}
            </div>
          </div>
        </div>

        <p className="partner-introduction">{partner.introduction}</p>

        <div className="partner-skill-tags">
          {partner.skills.map((skill) => <span key={skill}>{skill}</span>)}
        </div>

        <div className="partner-verifications">
          <span className={partner.identityVerified ? 'verified' : ''}>♙ 신원 인증</span>
          <span className={partner.contactVerified ? 'verified' : ''}>☎ 연락처 인증</span>
          <span className={partner.available ? 'available' : 'inactive'}>
            ● {partner.available ? '활동가능' : '활동불가'}
          </span>
          <span className={partner.onsiteAvailable ? 'available' : 'inactive'}>
            ● {partner.onsiteAvailable ? '상주가능' : '상주불가'}
          </span>
        </div>
      </div>

      <aside className="partner-card-stats">
        <div className="partner-card-buttons">
          {partner.hasVideo && (
            <button type="button" className="partner-video-button" onClick={openVideo}>
              ▣ 전문가 영상 인터뷰
            </button>
          )}
          {loginUser?.role === 'CLIENT' && (
            <button type="button" className="partner-quote-button" onClick={requestQuote}>
              견적 요청하기
            </button>
          )}
        </div>

        <div className="partner-interest">
          <strong>♥ {partner.favoriteCount}명이 관심을 갖고 있습니다.</strong>
          <button
            type="button"
            className={favorite ? 'favorite-button active' : 'favorite-button'}
            onClick={onToggleFavorite}
            aria-label="관심 파트너"
          >
            {favorite ? '♥' : '♡'}
          </button>
        </div>

        <div className="partner-rating">
          <span>★★★★★</span>
          <strong>{partner.rating} / 평가 {partner.reviewCount}개</strong>
        </div>

        <dl>
          <div><dt>계약 프로젝트</dt><dd>{partner.contractCount}건</dd></div>
          <div><dt>포트폴리오</dt><dd>{partner.portfolioCount}개</dd></div>
          <div><dt>누적금액</dt><dd>{partner.totalAmount.toLocaleString()}만원</dd></div>
        </dl>
      </aside>
    </article>
  );
}
