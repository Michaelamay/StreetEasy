package com.streeteasy.listings.model;

public class ScoredListing {

    private Listing listing;
    private double relevanceScore;

    public ScoredListing(Listing listing, double relevanceScore) {
        this.listing = listing;
        this.relevanceScore = relevanceScore;
    }

    public Listing getListing() { return listing; }
    public double getRelevanceScore() { return relevanceScore; }
}
