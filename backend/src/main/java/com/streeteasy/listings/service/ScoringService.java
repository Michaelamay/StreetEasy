package com.streeteasy.listings.service;

import com.streeteasy.listings.model.Listing;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class ScoringService {

    private static final double BUDGET_WEIGHT = 0.6;
    private static final double RECENCY_WEIGHT = 0.4;
    private static final double RECENCY_HALF_LIFE_DAYS = 30.0;

    public double score(Listing listing, Double targetBudget) {
        double recencyScore = computeRecencyScore(listing.getListedDate());

        if (targetBudget == null || targetBudget <= 0) {
            return Math.round(recencyScore * 100.0) / 100.0;
        }

        double budgetScore = computeBudgetScore(listing.getPrice(), targetBudget);
        double combined = BUDGET_WEIGHT * budgetScore + RECENCY_WEIGHT * recencyScore;
        return Math.round(combined * 100.0) / 100.0;
    }

    double computeBudgetScore(double price, double targetBudget) {
        double deviation = Math.abs(price - targetBudget) / targetBudget;
        return Math.max(0.0, 1.0 - deviation);
    }

    double computeRecencyScore(String listedDate) {
        LocalDate listed = LocalDate.parse(listedDate);
        long daysSince = ChronoUnit.DAYS.between(listed, LocalDate.now());
        if (daysSince < 0) {
            daysSince = 0;
        }
        return 1.0 / (1.0 + daysSince / RECENCY_HALF_LIFE_DAYS);
    }
}
