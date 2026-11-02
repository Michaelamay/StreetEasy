import { useCallback, useState } from 'react';
import { searchListings } from './api/listings';
import ListingCard from './components/ListingCard';
import Pagination from './components/Pagination';
import SearchForm from './components/SearchForm';
import { SearchParams, SearchResponse } from './types';

const PAGE_SIZE = 5;

export default function App() {
  const [response, setResponse] = useState<SearchResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [currentFilters, setCurrentFilters] = useState<Omit<SearchParams, 'page' | 'pageSize'>>({});
  const [hasSearched, setHasSearched] = useState(false);

  const doSearch = useCallback(async (filters: Omit<SearchParams, 'page' | 'pageSize'>, page: number) => {
    setLoading(true);
    setError(null);
    try {
      const result = await searchListings({ ...filters, page, pageSize: PAGE_SIZE });
      setResponse(result);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'An unexpected error occurred');
      setResponse(null);
    } finally {
      setLoading(false);
    }
  }, []);

  function handleSearch(filters: Omit<SearchParams, 'page' | 'pageSize'>) {
    setCurrentFilters(filters);
    setHasSearched(true);
    doSearch(filters, 1);
  }

  function handlePageChange(page: number) {
    doSearch(currentFilters, page);
  }

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto', padding: '16px' }}>
      <h1>Listing Search</h1>
      <SearchForm onSearch={handleSearch} loading={loading} />

      {error && (
        <div style={{ marginTop: '16px', padding: '12px', background: '#fee', border: '1px solid #c00', borderRadius: '4px', color: '#c00' }}>
          {error}
        </div>
      )}

      {loading && <p style={{ marginTop: '16px' }}>Loading…</p>}

      {!loading && hasSearched && response && response.results.length === 0 && (
        <p style={{ marginTop: '16px', color: '#666' }}>No listings found matching your criteria.</p>
      )}

      {!loading && response && response.results.length > 0 && (
        <div style={{ marginTop: '16px' }}>
          <p style={{ color: '#666', marginBottom: '8px' }}>
            {response.totalResults} result{response.totalResults !== 1 ? 's' : ''} found
          </p>
          {response.results.map((item, idx) => (
            <ListingCard key={`${item.listing.source}-${item.listing.id}-${idx}`} item={item} />
          ))}
          <Pagination page={response.page} totalPages={response.totalPages} onPageChange={handlePageChange} />
        </div>
      )}
    </div>
  );
}
