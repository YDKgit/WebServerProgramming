import { useEffect, useState } from 'react';
import { partnersApi } from '../../api/api';
import ErrorBox from '../common/ErrorBox.jsx';
import Loading from '../common/Loading.jsx';
import MarketplaceSearchHeader from '../common/MarketplaceSearchHeader.jsx';
import PartnerCard from './PartnerCard.jsx';
import PartnersFilterSidebar from './PartnersFilterSidebar.jsx';

const initialFilters = {
  infoType: 'basic',
  businessTypes: [],
  onsiteOnly: false,
  activeOnly: false,
  fields: [],
  region: 'ALL',
};

export default function PartnersPage() {
  const loginUser = JSON.parse(localStorage.getItem('loginUser') || 'null');
  const [searchInput, setSearchInput] = useState('');
  const [keyword, setKeyword] = useState('');
  const [filters, setFilters] = useState(initialFilters);
  const [sort, setSort] = useState('default');
  const [partners, setPartners] = useState([]);
  const [favorites, setFavorites] = useState(new Set());
  const [favoritesOnly, setFavoritesOnly] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let ignore = false;
    setLoading(true);
    setError('');
    partnersApi.getPartners({ keyword, ...filters, sort })
      .then((result) => {
        if (!ignore) setPartners(result.content || []);
      })
      .catch((event) => {
        if (!ignore) {
          console.error('파트너 목록 조회 실패:', event);
          setError(event.message || '파트너 목록을 불러오지 못했습니다.');
        }
      })
      .finally(() => {
        if (!ignore) setLoading(false);
      });
    return () => {
      ignore = true;
    };
  }, [keyword, filters, sort]);

  const changeFilter = (key, value) => {
    setFilters((current) => ({ ...current, [key]: value }));
  };

  const toggleArrayFilter = (key, value) => {
    setFilters((current) => ({
      ...current,
      [key]: current[key].includes(value)
        ? current[key].filter((item) => item !== value)
        : [...current[key], value],
    }));
  };

  const toggleFavorite = (partnerId) => {
    setFavorites((current) => {
      const next = new Set(current);
      if (next.has(partnerId)) next.delete(partnerId);
      else next.add(partnerId);
      return next;
    });
  };

  const visiblePartners = favoritesOnly
    ? partners.filter((partner) => favorites.has(partner.id))
    : partners;

  return (
    <section className="marketplace-page partners-page">
      <MarketplaceSearchHeader
        accentText="IT 전문가 파트너"
        title="를 찾아보세요."
        placeholder="원하는 기술 및 주요 키워드로 검색 해보세요."
        value={searchInput}
        onChange={(event) => setSearchInput(event.target.value)}
        onSubmit={(event) => {
          event.preventDefault();
          setKeyword(searchInput.trim());
        }}
      />

      <div className="market-toolbar">
        <button
          type="button"
          className={favoritesOnly ? 'favorite-partners-toggle active' : 'favorite-partners-toggle'}
          onClick={() => setFavoritesOnly((current) => !current)}
        >
          ♥ 관심 파트너스 보기
        </button>
        <select value={sort} onChange={(event) => setSort(event.target.value)}>
          <option value="default">프리모아 기본정렬</option>
          <option value="rating">별점 높은순</option>
          <option value="contracts">계약 많은순</option>
          <option value="portfolio">포트폴리오 많은순</option>
        </select>
      </div>

      <div className="market-layout">
        <PartnersFilterSidebar
          filters={filters}
          onChange={changeFilter}
          onToggleArray={toggleArrayFilter}
        />

        <div className="market-results">
          {error && <ErrorBox message={error} />}
          {loading && <Loading message="파트너를 불러오는 중입니다." />}
          {!loading && !error && (
            visiblePartners.length === 0 ? (
              <div className="empty-box">등록된 파트너가 없습니다.</div>
            ) : (
              <div className="partner-list">
                {visiblePartners.map((partner) => (
                  <PartnerCard
                    partner={partner}
                    loginUser={loginUser}
                    favorite={favorites.has(partner.id)}
                    onToggleFavorite={() => toggleFavorite(partner.id)}
                    key={partner.id}
                  />
                ))}
              </div>
            )
          )}
        </div>
      </div>
    </section>
  );
}
