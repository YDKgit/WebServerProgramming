export default function MarketplaceSearchHeader({
  accentText,
  title,
  placeholder,
  value,
  onChange,
  onSubmit,
  accent = 'orange',
}) {
  return (
    <form className={`market-search-header ${accent}`} onSubmit={onSubmit}>
      <h1>
        <span>{accentText}</span>{title}
      </h1>
      <div className="market-search-control">
        <input
          value={value}
          onChange={onChange}
          placeholder={placeholder}
          aria-label={placeholder}
        />
        <button type="submit" aria-label="검색">
          <span aria-hidden="true">⌕</span>
        </button>
      </div>
    </form>
  );
}
