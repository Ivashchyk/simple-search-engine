package com.ivashchyk.simplesearchengine.api.model;

public class SearchLine {

    private String toSearch;

    public SearchLine() {
    }

    public SearchLine(String toSearch) {
        this.toSearch = toSearch;
    }

    public String getToSearch() {
        return toSearch;
    }

    public void setToSearch(String toSearch) {
        this.toSearch = toSearch;
    }
}
