package com.streeteasy.listings.model;

public class SearchRequest {

    private Double minPrice;
    private Double maxPrice;
    private Integer minBedrooms;
    private String city;
    private String keyword;
    private Double targetBudget;
    private int page = 1;
    private int pageSize = 10;

    public Double getMinPrice() { return minPrice; }
    public void setMinPrice(Double minPrice) { this.minPrice = minPrice; }

    public Double getMaxPrice() { return maxPrice; }
    public void setMaxPrice(Double maxPrice) { this.maxPrice = maxPrice; }

    public Integer getMinBedrooms() { return minBedrooms; }
    public void setMinBedrooms(Integer minBedrooms) { this.minBedrooms = minBedrooms; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public Double getTargetBudget() { return targetBudget; }
    public void setTargetBudget(Double targetBudget) { this.targetBudget = targetBudget; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
}
