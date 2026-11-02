import { SearchParams, SearchResponse } from '../types';

export async function searchListings(params: SearchParams): Promise<SearchResponse> {
  const query = new URLSearchParams();

  if (params.minPrice) query.set('minPrice', params.minPrice);
  if (params.maxPrice) query.set('maxPrice', params.maxPrice);
  if (params.minBedrooms) query.set('minBedrooms', params.minBedrooms);
  if (params.city) query.set('city', params.city);
  if (params.keyword) query.set('keyword', params.keyword);
  if (params.targetBudget) query.set('targetBudget', params.targetBudget);
  query.set('page', String(params.page));
  query.set('pageSize', String(params.pageSize));

  const response = await fetch(`/api/listings/search?${query.toString()}`);

  if (!response.ok) {
    const body = await response.json().catch(() => null);
    throw new Error(body?.error || `Request failed with status ${response.status}`);
  }

  return response.json();
}
