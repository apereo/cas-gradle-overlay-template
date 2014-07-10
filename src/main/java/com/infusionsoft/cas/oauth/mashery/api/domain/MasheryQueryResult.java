package com.infusionsoft.cas.oauth.mashery.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasheryQueryResult<T> {

    private int totalItems;
    private int totalPages;
    private int itemsPerPage;
    private int currentPage;
    private Set<T> items;

    public int getTotalItems() {
        return totalItems;
    }

    @JsonProperty("total_items")
    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    @JsonProperty("total_pages")
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    @JsonProperty("items_per_page")
    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    @JsonProperty("current_page")
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public Set<T> getItems() {
        return items;
    }

    public void setItems(Set<T> items) {
        this.items = items;
    }
}
