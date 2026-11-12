package com.streeteasy.listings.controller;

import com.streeteasy.listings.model.SearchRequest;
import com.streeteasy.listings.model.SearchResponse;
import com.streeteasy.listings.repository.ListingRepository;
import com.streeteasy.listings.service.ListingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
@CrossOrigin(origins = "*")
public class ListingController {

    private final ListingService listingService;
    private final ListingRepository listingRepository;

    public ListingController(ListingService listingService, ListingRepository listingRepository) {
        this.listingService = listingService;
        this.listingRepository = listingRepository;
    }

    @GetMapping("/cities")
    public List<String> getCities() {
        return listingRepository.findAll().stream()
                .map(l -> l.getCity())
                .distinct()
                .sorted()
                .toList();
    }

    @GetMapping("/search")
    public SearchResponse search(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer minBedrooms,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double targetBudget,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        SearchRequest request = new SearchRequest();
        request.setMinPrice(minPrice);
        request.setMaxPrice(maxPrice);
        request.setMinBedrooms(minBedrooms);
        request.setCity(city);
        request.setKeyword(keyword);
        request.setTargetBudget(targetBudget);
        request.setPage(page);
        request.setPageSize(pageSize);
        return listingService.search(request);
    }
}
