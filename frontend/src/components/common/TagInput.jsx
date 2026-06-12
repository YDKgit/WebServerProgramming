import { useState } from 'react';

export default function TagInput({ value, onChange, max = 5, placeholder = '태그 입력' }) {
  const [input, setInput] = useState('');
  const [message, setMessage] = useState('');

  const addTag = () => {
    const next = input.trim();
    if (!next) return;
    if (value.includes(next)) {
      setMessage('이미 등록된 태그입니다.');
      return;
    }
    if (value.length >= max) {
      setMessage(`검색태그는 최대 ${max}개까지 등록할 수 있습니다.`);
      return;
    }
    onChange([...value, next]);
    setInput('');
    setMessage('');
  };

  const removeTag = (tag) => {
    onChange(value.filter((item) => item !== tag));
    setMessage('');
  };

  return (
    <div className="tag-input-control">
      <div className="tag-list">
        {value.map((tag) => (
          <span className="tag removable" key={tag}>
            {tag}
            <button type="button" onClick={() => removeTag(tag)} aria-label={`${tag} 삭제`}>×</button>
          </span>
        ))}
      </div>
      <div className="inline-form">
        <input
          value={input}
          placeholder={placeholder}
          onChange={(event) => setInput(event.target.value)}
          aria-describedby="tag-limit-help"
          onKeyDown={(event) => {
            if (event.key === 'Enter') {
              event.preventDefault();
              addTag();
            }
          }}
        />
        <button type="button" className="btn secondary" onClick={addTag}>추가</button>
      </div>
      <p className="tag-limit-help" id="tag-limit-help">
        쉼표 없이 한 개씩 입력해 주세요. 최대 {max}개까지 등록할 수 있습니다.
      </p>
      {message && <p className="field-error">{message}</p>}
    </div>
  );
}
