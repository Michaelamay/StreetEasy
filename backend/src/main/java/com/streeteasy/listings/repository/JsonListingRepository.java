package com.streeteasy.listings.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.streeteasy.listings.model.Listing;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Repository
public class JsonListingRepository implements ListingRepository {

    private List<Listing> listings = Collections.emptyList();

    @PostConstruct
    public void init() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = getClass().getResourceAsStream("/sample_listings.json")) {
            listings = mapper.readValue(is, new TypeReference<List<Listing>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to load sample_listings.json", e);
        }
    }

    @Override
    public List<Listing> findAll() {
        return Collections.unmodifiableList(listings);
    }
}
