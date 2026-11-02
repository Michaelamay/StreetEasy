export interface Listing {
  id: string;
  source: string;
  address: string;
  city: string;
  state: string;
  zip: string;
  price: number;
  bedrooms: number;
  bathrooms: number;
  sqft: number;
  latitude: number;
  longitude: number;
  listedDate: string;
  status: string;
  description: string;
}

export interface ScoredListing {
  listing: Listing;
  relevanceScore: number;
}

export interface SearchResponse {
  results: ScoredListing[];
  page: number;
  pageSize: number;
  totalResults: number;
  totalPages: number;
}

export interface SearchParams {
  minPrice?: string;
  maxPrice?: string;
  minBedrooms?: string;
  city?: string;
  keyword?: string;
  targetBudget?: string;
  page: number;
  pageSize: number;
}
