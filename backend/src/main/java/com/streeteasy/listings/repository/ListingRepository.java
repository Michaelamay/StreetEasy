package com.streeteasy.listings.repository;

import com.streeteasy.listings.model.Listing;

import java.util.List;

public interface ListingRepository {
    List<Listing> findAll();
}
