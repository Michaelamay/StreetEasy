import { ScoredListing } from '../types';

interface Props {
  item: ScoredListing;
}

export default function ListingCard({ item }: Props) {
  const { listing, relevanceScore } = item;

  return (
    <div style={{
      border: '1px solid #ddd',
      borderRadius: '6px',
      padding: '12px 16px',
      marginBottom: '8px',
    }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline' }}>
        <strong>{listing.address}</strong>
        <span style={{ color: '#666', fontSize: '0.9em' }}>Score: {relevanceScore.toFixed(2)}</span>
      </div>
      <div style={{ marginTop: '4px', color: '#444' }}>
        {listing.city}, {listing.state} {listing.zip}
      </div>
      <div style={{ marginTop: '4px' }}>
        <span><strong>${listing.price.toLocaleString()}</strong></span>
        {' · '}
        <span>{listing.bedrooms} bed{listing.bedrooms !== 1 ? 's' : ''}</span>
        {' · '}
        <span>{listing.bathrooms} bath{listing.bathrooms !== 1 ? 's' : ''}</span>
        {' · '}
        <span>{listing.sqft.toLocaleString()} sqft</span>
      </div>
      <div style={{ marginTop: '4px', fontSize: '0.85em', color: '#666' }}>
        {listing.description}
      </div>
      <div style={{ marginTop: '4px', fontSize: '0.8em', color: '#999' }}>
        Listed: {listing.listedDate} · Status: {listing.status} · Source: {listing.source}
      </div>
    </div>
  );
}
