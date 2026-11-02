package com.streeteasy.listings.service;

import com.streeteasy.listings.exception.ValidationException;
import com.streeteasy.listings.model.Listing;
import com.streeteasy.listings.model.SearchRequest;
import com.streeteasy.listings.model.SearchResponse;
import com.streeteasy.listings.repository.ListingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ListingServiceTest {

    private ListingService listingService;
    private ListingRepository repository;

    @BeforeEach
    void setUp() {
        repository = mock(ListingRepository.class);
        ScoringService scoringService = new ScoringService();
        listingService = new ListingService(repository, scoringService);
    }

    @Test
    void filterByCity() {
        when(repository.findAll()).thenReturn(List.of(
                makeListing("1", "Springfield", 400000, 3, "Nice house"),
                makeListing("2", "Fairfax", 350000, 2, "Cozy condo")
        ));
        SearchRequest req = new SearchRequest();
        req.setCity("Springfield");
        SearchResponse resp = listingService.search(req);
        assertEquals(1, resp.getTotalResults());
        assertEquals("Springfield", resp.getResults().get(0).getListing().getCity());
    }

    @Test
    void filterByCityIsCaseInsensitive() {
        when(repository.findAll()).thenReturn(List.of(
                makeListing("1", "Springfield", 400000, 3, "Nice house")
        ));
        SearchRequest req = new SearchRequest();
        req.setCity("springfield");
        SearchResponse resp = listingService.search(req);
        assertEquals(1, resp.getTotalResults());
    }

    @Test
    void filterByPriceRange() {
        when(repository.findAll()).thenReturn(List.of(
                makeListing("1", "City", 300000, 2, "Cheap"),
                makeListing("2", "City", 500000, 3, "Mid"),
                makeListing("3", "City", 800000, 4, "Expensive")
        ));
        SearchRequest req = new SearchRequest();
        req.setMinPrice(400000.0);
        req.setMaxPrice(600000.0);
        SearchResponse resp = listingService.search(req);
        assertEquals(1, resp.getTotalResults());
        assertEquals(500000, resp.getResults().get(0).getListing().getPrice());
    }

    @Test
    void filterByMinBedrooms() {
        when(repository.findAll()).thenReturn(List.of(
                makeListing("1", "City", 400000, 1, "Studio"),
                makeListing("2", "City", 400000, 3, "Family home")
        ));
        SearchRequest req = new SearchRequest();
        req.setMinBedrooms(2);
        SearchResponse resp = listingService.search(req);
        assertEquals(1, resp.getTotalResults());
        assertEquals(3, resp.getResults().get(0).getListing().getBedrooms());
    }

    @Test
    void filterByKeyword() {
        when(repository.findAll()).thenReturn(List.of(
                makeListing("1", "City", 400000, 2, "Pet friendly condo"),
                makeListing("2", "City", 400000, 2, "No animals allowed")
        ));
        SearchRequest req = new SearchRequest();
        req.setKeyword("pet");
        SearchResponse resp = listingService.search(req);
        assertEquals(1, resp.getTotalResults());
    }

    @Test
    void noMatchesReturnsEmptyResults() {
        when(repository.findAll()).thenReturn(List.of(
                makeListing("1", "Springfield", 400000, 2, "A house")
        ));
        SearchRequest req = new SearchRequest();
        req.setCity("Nonexistent");
        SearchResponse resp = listingService.search(req);
        assertEquals(0, resp.getTotalResults());
        assertEquals(0, resp.getTotalPages());
        assertTrue(resp.getResults().isEmpty());
    }

    @Test
    void paginationWorks() {
        when(repository.findAll()).thenReturn(List.of(
                makeListing("1", "City", 400000, 2, "A"),
                makeListing("2", "City", 410000, 2, "B"),
                makeListing("3", "City", 420000, 2, "C"),
                makeListing("4", "City", 430000, 2, "D"),
                makeListing("5", "City", 440000, 2, "E")
        ));
        SearchRequest req = new SearchRequest();
        req.setPageSize(2);
        req.setPage(1);
        SearchResponse resp = listingService.search(req);
        assertEquals(2, resp.getResults().size());
        assertEquals(5, resp.getTotalResults());
        assertEquals(3, resp.getTotalPages());

        req.setPage(3);
        resp = listingService.search(req);
        assertEquals(1, resp.getResults().size());
    }

    @Test
    void pageOutOfRangeReturnsEmpty() {
        when(repository.findAll()).thenReturn(List.of(
                makeListing("1", "City", 400000, 2, "A")
        ));
        SearchRequest req = new SearchRequest();
        req.setPage(99);
        SearchResponse resp = listingService.search(req);
        assertTrue(resp.getResults().isEmpty());
    }

    @Test
    void minPriceGreaterThanMaxPriceThrows() {
        SearchRequest req = new SearchRequest();
        req.setMinPrice(500000.0);
        req.setMaxPrice(300000.0);
        assertThrows(ValidationException.class, () -> listingService.search(req));
    }

    @Test
    void pageSizeZeroThrows() {
        SearchRequest req = new SearchRequest();
        req.setPageSize(0);
        assertThrows(ValidationException.class, () -> listingService.search(req));
    }

    @Test
    void negativePageThrows() {
        SearchRequest req = new SearchRequest();
        req.setPage(-1);
        assertThrows(ValidationException.class, () -> listingService.search(req));
    }

    @Test
    void negativeMinPriceThrows() {
        SearchRequest req = new SearchRequest();
        req.setMinPrice(-100.0);
        assertThrows(ValidationException.class, () -> listingService.search(req));
    }

    @Test
    void resultsAreSortedByScoreDescending() {
        when(repository.findAll()).thenReturn(List.of(
                makeListing("1", "City", 300000, 2, "A"),
                makeListing("2", "City", 500000, 2, "B"),
                makeListing("3", "City", 490000, 2, "C")
        ));
        SearchRequest req = new SearchRequest();
        req.setTargetBudget(500000.0);
        SearchResponse resp = listingService.search(req);
        assertTrue(resp.getResults().get(0).getRelevanceScore() >= resp.getResults().get(1).getRelevanceScore());
        assertTrue(resp.getResults().get(1).getRelevanceScore() >= resp.getResults().get(2).getRelevanceScore());
    }

    @Test
    void multipleFiltersApplyTogether() {
        when(repository.findAll()).thenReturn(List.of(
                makeListing("1", "Springfield", 400000, 3, "Pet friendly"),
                makeListing("2", "Springfield", 400000, 1, "Pet friendly"),
                makeListing("3", "Fairfax", 400000, 3, "Pet friendly"),
                makeListing("4", "Springfield", 400000, 3, "No animals")
        ));
        SearchRequest req = new SearchRequest();
        req.setCity("Springfield");
        req.setMinBedrooms(2);
        req.setKeyword("pet");
        SearchResponse resp = listingService.search(req);
        assertEquals(1, resp.getTotalResults());
        assertEquals("1", resp.getResults().get(0).getListing().getId());
    }

    private Listing makeListing(String id, String city, double price, int bedrooms, String description) {
        Listing l = new Listing();
        l.setId(id);
        l.setSource("TEST");
        l.setAddress("123 Test St");
        l.setCity(city);
        l.setState("TS");
        l.setZip("00000");
        l.setPrice(price);
        l.setBedrooms(bedrooms);
        l.setBathrooms(1.0);
        l.setSqft(1000);
        l.setListedDate(LocalDate.now().toString());
        l.setStatus("active");
        l.setDescription(description);
        return l;
    }
}
