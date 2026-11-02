package com.streeteasy.listings.service;

import com.streeteasy.listings.model.Listing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ScoringServiceTest {

    private ScoringService scoringService;

    @BeforeEach
    void setUp() {
        scoringService = new ScoringService();
    }

    @Test
    void exactBudgetMatchScoresHigh() {
        Listing listing = makeListing(500000, LocalDate.now().toString());
        double score = scoringService.score(listing, 500000.0);
        assertTrue(score > 0.9, "Exact budget match with today's listing should score > 0.9");
    }

    @Test
    void farFromBudgetScoresLow() {
        Listing listing = makeListing(1000000, LocalDate.now().toString());
        double score = scoringService.score(listing, 500000.0);
        assertTrue(score < 0.5, "Price double the budget should score low");
    }

    @Test
    void priceExceedingBudgetByMoreThan100PercentScoresZeroBudgetComponent() {
        Listing listing = makeListing(1100000, LocalDate.now().toString());
        double score = scoringService.score(listing, 500000.0);
        double recencyOnly = scoringService.score(listing, null);
        assertTrue(score <= recencyOnly * 0.4 + 0.01, "Budget component should be 0 when deviation > 100%");
    }

    @Test
    void recentListingScoresHigherThanOld() {
        Listing recent = makeListing(500000, LocalDate.now().toString());
        Listing old = makeListing(500000, LocalDate.now().minusDays(90).toString());
        double recentScore = scoringService.score(recent, 500000.0);
        double oldScore = scoringService.score(old, 500000.0);
        assertTrue(recentScore > oldScore);
    }

    @Test
    void nullBudgetScoresPurelyOnRecency() {
        Listing listing = makeListing(500000, LocalDate.now().toString());
        double score = scoringService.score(listing, null);
        assertTrue(score > 0.9, "Today's listing with no budget should score ~1.0 on recency alone");
    }

    @Test
    void zeroBudgetScoresPurelyOnRecency() {
        Listing listing = makeListing(500000, LocalDate.now().toString());
        double scoreNull = scoringService.score(listing, null);
        double scoreZero = scoringService.score(listing, 0.0);
        assertEquals(scoreNull, scoreZero);
    }

    @Test
    void tiedScoresForIdenticalListings() {
        Listing a = makeListing(450000, "2026-09-01");
        Listing b = makeListing(450000, "2026-09-01");
        assertEquals(scoringService.score(a, 500000.0), scoringService.score(b, 500000.0));
    }

    @Test
    void futureDateTreatedAsToday() {
        Listing future = makeListing(500000, LocalDate.now().plusDays(5).toString());
        Listing today = makeListing(500000, LocalDate.now().toString());
        assertEquals(scoringService.score(future, 500000.0), scoringService.score(today, 500000.0));
    }

    private Listing makeListing(double price, String listedDate) {
        Listing l = new Listing();
        l.setId("test");
        l.setSource("TEST");
        l.setAddress("123 Test St");
        l.setCity("TestCity");
        l.setState("TS");
        l.setZip("00000");
        l.setPrice(price);
        l.setBedrooms(2);
        l.setBathrooms(1.0);
        l.setSqft(1000);
        l.setListedDate(listedDate);
        l.setStatus("active");
        l.setDescription("A test listing");
        return l;
    }
}
