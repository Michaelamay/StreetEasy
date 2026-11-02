# Listing Search Service

A full-stack property listing search application with a Java (Spring Boot) backend and a React (TypeScript) frontend.

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- Node.js 18+

### Backend

```bash
cd backend
mvn spring-boot:run
```

The API starts on http://localhost:8080.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

The UI starts on http://localhost:5173 and proxies API requests to the backend.

## API

**GET /api/listings/search**

| Parameter     | Type    | Description                              |
|---------------|---------|------------------------------------------|
| minPrice      | number  | Minimum list price                       |
| maxPrice      | number  | Maximum list price                       |
| minBedrooms   | integer | Minimum number of bedrooms               |
| city          | string  | City name (case-insensitive)             |
| keyword       | string  | Free-text search against description     |
| targetBudget  | number  | Budget for relevance scoring             |
| page          | integer | Page number (default: 1)                 |
| pageSize      | integer | Results per page (default: 10)           |

## Scoring Approach

Results are ranked by a **relevance score** combining two factors:

```
relevanceScore = 0.6 × budgetScore + 0.4 × recencyScore
```

**Budget score** — how close the listing price is to the user's target budget, normalized by that budget:

```
budgetScore = max(0, 1 − |price − targetBudget| / targetBudget)
```

A perfect match (price equals budget) scores 1.0. A listing at double or half the budget scores 0.0. Normalizing by the budget means a $10K gap matters more on a $100K budget than a $1M one — matching real buyer sensitivity.

**Recency score** — how recently the listing appeared, with a 30-day half-life:

```
recencyScore = 1 / (1 + daysSinceListed / 30)
```

Today's listings score ~1.0. A listing 30 days old scores ~0.5. Older listings taper off but never hit zero, keeping them discoverable while favoring fresh inventory.

If no `targetBudget` is provided, results are ranked purely by recency.

**Why 60/40 weighting?** Budget fit is the primary signal in real estate search — buyers have hard financial constraints. Recency acts as a tiebreaker and freshness signal. The weights are configurable constants (`ScoringService.java`).

## Trade-offs

- **In-memory data store**: All listings are loaded from JSON at startup and filtered in Java. This is appropriate for the sample size (~12 listings) and avoids database setup complexity. For production, this would be backed by a database with indexed queries.
- **No deduplication**: The sample data contains duplicate listings from different MLS sources (e.g., "123 Main St" from MLS_A and MLS_B). These are intentionally left as separate results — deduplication across MLS feeds is a complex domain problem (address normalization, fuzzy matching) that's out of scope here but would be a natural extension.
- **Simple keyword search**: Description matching uses case-insensitive substring search. A production system would use full-text search (e.g., Elasticsearch) with stemming and relevance ranking.
- **No authentication or rate limiting**: Appropriate for a local demo; production would add both.

## Tests

```bash
cd backend
mvn test
```

The test suite covers:
- **Scoring**: exact budget match, far-from-budget, recency decay, null budget fallback, tied scores, future dates
- **Filtering**: city (case-insensitive), price range, min bedrooms, keyword, combined filters
- **Validation**: minPrice > maxPrice, pageSize ≤ 0, negative page, negative prices
- **Pagination**: multi-page results, last page with partial results, page out of range
- **Integration**: end-to-end controller tests against the real sample data
