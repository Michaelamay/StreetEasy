import { FormEvent, useState } from 'react';
import { SearchParams } from '../types';

interface Props {
  onSearch: (params: Omit<SearchParams, 'page' | 'pageSize'>) => void;
  loading: boolean;
}

export default function SearchForm({ onSearch, loading }: Props) {
  const [minPrice, setMinPrice] = useState('');
  const [maxPrice, setMaxPrice] = useState('');
  const [minBedrooms, setMinBedrooms] = useState('');
  const [city, setCity] = useState('');
  const [keyword, setKeyword] = useState('');
  const [targetBudget, setTargetBudget] = useState('');

  function handleSubmit(e: FormEvent) {
    e.preventDefault();
    onSearch({ minPrice, maxPrice, minBedrooms, city, keyword, targetBudget });
  }

  return (
    <form onSubmit={handleSubmit} style={{ display: 'flex', flexWrap: 'wrap', gap: '12px', alignItems: 'flex-end' }}>
      <label>
        Min Price
        <input type="number" value={minPrice} onChange={e => setMinPrice(e.target.value)} placeholder="e.g. 300000" />
      </label>
      <label>
        Max Price
        <input type="number" value={maxPrice} onChange={e => setMaxPrice(e.target.value)} placeholder="e.g. 600000" />
      </label>
      <label>
        Min Bedrooms
        <input type="number" value={minBedrooms} onChange={e => setMinBedrooms(e.target.value)} placeholder="e.g. 2" />
      </label>
      <label>
        City
        <input type="text" value={city} onChange={e => setCity(e.target.value)} placeholder="e.g. Springfield" />
      </label>
      <label>
        Keyword
        <input type="text" value={keyword} onChange={e => setKeyword(e.target.value)} placeholder="e.g. pet friendly" />
      </label>
      <label>
        Target Budget
        <input type="number" value={targetBudget} onChange={e => setTargetBudget(e.target.value)} placeholder="e.g. 500000" />
      </label>
      <button type="submit" disabled={loading}>
        {loading ? 'Searching…' : 'Search'}
      </button>
    </form>
  );
}
