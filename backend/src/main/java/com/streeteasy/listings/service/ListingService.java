package com.streeteasy.listings.service;

import com.streeteasy.listings.exception.ValidationException;
import com.streeteasy.listings.model.*;
import com.streeteasy.listings.repository.ListingRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListingService {

    private final ListingRepository repository;
    private final ScoringService scoringService;

    public ListingService(ListingRepository repository, ScoringService scoringService) {
        this.repository = repository;
        this.scoringService = scoringService;
    }

    public SearchResponse search(SearchRequest request) {
        validate(request);

        List<Listing> filtered = repository.findAll().stream()
                .filter(l -> matchesFilters(l, request))
                .collect(Collectors.toList());

        List<ScoredListing> scored = filtered.stream()
                .map(l -> new ScoredListing(l, scoringService.score(l, request.getTargetBudget())))
                .sorted(Comparator.comparingDouble(ScoredListing::getRelevanceScore).reversed())
                .collect(Collectors.toList());

        int totalResults = scored.size();
        int pageSize = request.getPageSize();
        int totalPages = totalResults == 0 ? 0 : (int) Math.ceil((double) totalResults / pageSize);
        int page = request.getPage();

        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalResults);

        List<ScoredListing> pageResults;
        if (fromIndex >= totalResults) {
            pageResults = List.of();
        } else {
            pageResults = scored.subList(fromIndex, toIndex);
        }

        return new SearchResponse(pageResults, page, pageSize, totalResults, totalPages);
    }

    private void validate(SearchRequest request) {
        if (request.getMinPrice() != null && request.getMaxPrice() != null
                && request.getMinPrice() > request.getMaxPrice()) {
            throw new ValidationException("minPrice cannot be greater than maxPrice");
        }
        if (request.getPageSize() <= 0) {
            throw new ValidationException("pageSize must be greater than 0");
        }
        if (request.getPage() <= 0) {
            throw new ValidationException("page must be greater than 0");
        }
        if (request.getMinPrice() != null && request.getMinPrice() < 0) {
            throw new ValidationException("minPrice cannot be negative");
        }
        if (request.getMaxPrice() != null && request.getMaxPrice() < 0) {
            throw new ValidationException("maxPrice cannot be negative");
        }
        if (request.getTargetBudget() != null && request.getTargetBudget() < 0) {
            throw new ValidationException("targetBudget cannot be negative");
        }
    }

    private boolean matchesFilters(Listing listing, SearchRequest request) {
        if (request.getMinPrice() != null && listing.getPrice() < request.getMinPrice()) {
            return false;
        }
        if (request.getMaxPrice() != null && listing.getPrice() > request.getMaxPrice()) {
            return false;
        }
        if (request.getMinBedrooms() != null && listing.getBedrooms() < request.getMinBedrooms()) {
            return false;
        }
        if (request.getCity() != null && !request.getCity().isBlank()
                && !listing.getCity().equalsIgnoreCase(request.getCity().trim())) {
            return false;
        }
        if (request.getKeyword() != null && !request.getKeyword().isBlank()
                && !listing.getDescription().toLowerCase().contains(request.getKeyword().trim().toLowerCase())) {
            return false;
        }
        return true;
    }
}
