package com.streeteasy.listings.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ListingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void searchWithNoFiltersReturnsResults() throws Exception {
        mockMvc.perform(get("/api/listings/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.totalResults").isNumber())
                .andExpect(jsonPath("$.page").value(1));
    }

    @Test
    void searchWithCityFilterWorks() throws Exception {
        mockMvc.perform(get("/api/listings/search").param("city", "Springfield"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalResults").isNumber());
    }

    @Test
    void searchWithInvalidPriceRangeReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/listings/search")
                        .param("minPrice", "500000")
                        .param("maxPrice", "300000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("minPrice cannot be greater than maxPrice"));
    }

    @Test
    void searchWithZeroPageSizeReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/listings/search").param("pageSize", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("pageSize must be greater than 0"));
    }

    @Test
    void searchWithPaginationWorks() throws Exception {
        mockMvc.perform(get("/api/listings/search")
                        .param("pageSize", "3")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.length()").value(3))
                .andExpect(jsonPath("$.page").value(1));
    }

    @Test
    void searchWithTargetBudgetReturnsScores() throws Exception {
        mockMvc.perform(get("/api/listings/search").param("targetBudget", "500000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].relevanceScore").isNumber());
    }

    @Test
    void searchWithKeywordWorks() throws Exception {
        mockMvc.perform(get("/api/listings/search").param("keyword", "pet"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalResults").isNumber());
    }
}
