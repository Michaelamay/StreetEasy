package com.streeteasy.listings.model;

import java.util.List;

public class SearchResponse {

    private List<ScoredListing> results;
    private int page;
    private int pageSize;
    private int totalResults;
    private int totalPages;

    public SearchResponse(List<ScoredListing> results, int page, int pageSize, int totalResults, int totalPages) {
        this.results = results;
        this.page = page;
        this.pageSize = pageSize;
        this.totalResults = totalResults;
        this.totalPages = totalPages;
    }

    public List<ScoredListing> getResults() { return results; }
    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
    public int getTotalResults() { return totalResults; }
    public int getTotalPages() { return totalPages; }
}
