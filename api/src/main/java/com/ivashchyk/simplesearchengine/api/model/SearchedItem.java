package com.ivashchyk.simplesearchengine.api.model;

import java.util.LinkedList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@EqualsAndHashCode
public class SearchedItem implements Comparable<SearchedItem> {

    @Getter
    @Setter
    private Document document;

    @Getter
    private List<String> matchedKeys = new LinkedList<>();

    @Getter
    private List<String> mismatchedKeys = new LinkedList<>();

    @Getter
    private int matches = 0;

    public void addToMatchedKeys(String term) {
        matchedKeys.add(term);
    }

    public void addToMismatchedKeys(String term) {
        mismatchedKeys.add(term);
    }

    public void incMatches(int increment) {
        matches += increment;
    }

    @Override
    public int compareTo(SearchedItem other) {
        int matchedDiff = other.matches - this.matches;
        if (matchedDiff == 0) {
            return this.mismatchedKeys.size() - other.mismatchedKeys.size();
        } else {
            return matchedDiff;
        }
    }
}
