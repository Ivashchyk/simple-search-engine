package com.ivashchyk.simplesearchengine.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.ivashchyk.simplesearchengine.api.SearchApi;
import com.ivashchyk.simplesearchengine.api.model.Document;
import com.ivashchyk.simplesearchengine.api.model.SearchLine;
import com.ivashchyk.simplesearchengine.api.model.SearchedItem;
import com.ivashchyk.simplesearchengine.dao.DocumentDao;

import lombok.NonNull;

public class SearchService implements SearchApi {

    private final DocumentDao documentDao;

    private final int maxLimit;

    @Inject
    public SearchService(@NonNull DocumentDao documentDao, @Named("searchLimit") int maxLimit) {
        if (maxLimit < 1) {
            throw new IllegalArgumentException(String.format("Max limit should not be less then 1 and greater then '%d'", maxLimit));
        }
        this.documentDao = documentDao;
        this.maxLimit = maxLimit;
    }

    public List<Document> searchStrong(SearchLine searchLine) {
        return searchStrong(searchLine, DEFAULT_SEARCH_LIMIT);
    }

    public List<Document> searchStrong(SearchLine searchLine, int limit) {
        verifyNotBlank(searchLine.getToSearch());
        verifyLegalLimit(limit, maxLimit);
        Set<String> fileNames = documentDao.getAllFileNames();
        if (CollectionUtils.isEmpty(fileNames)) {
            return Collections.emptyList();
        }
        Set<String> terms = new HashSet<>(Arrays.asList(searchLine.getToSearch().split(" ")));
        List<Document> result = new LinkedList<>();
        Iterator<String> fileNamesIterator = fileNames.iterator();
        while (fileNamesIterator.hasNext() && result.size() < limit) {
            Document document = documentDao.find(fileNamesIterator.next());
            if (document != null) {
                Set<String> documentTerms = new HashSet<>(
                    Arrays.asList(Optional.ofNullable(document.getContent()).orElse("").split(" "))
                );
                if (CollectionUtils.containsAll(documentTerms, terms)) {
                    result.add(document);
                }
            }
        }
        return result;
    }

    public List<SearchedItem> searchSoft(SearchLine searchLine) {
        return searchSoft(searchLine, DEFAULT_SEARCH_LIMIT);
    }

    public List<SearchedItem> searchSoft(SearchLine searchLine, int limit) {
        verifyNotBlank(searchLine.getToSearch());
        verifyLegalLimit(limit, maxLimit);
        Set<String> fileNames = documentDao.getAllFileNames();
        if (CollectionUtils.isEmpty(fileNames)) {
            return Collections.emptyList();
        }
        List<SearchedItem> foundItems = new ArrayList<>();
        Set<String> terms = new HashSet<>(Arrays.asList(searchLine.getToSearch().split(" ")));
        for (String fileName : fileNames) {
            Document document = documentDao.find(fileName);
            if (document != null) {
                SearchedItem searchedItem = new SearchedItem();
                searchedItem.setDocument(document);
                for (String term : terms) {
                    int matches = StringUtils.countMatches(
                        Optional.ofNullable(document.getContent()).orElse(""),
                        term
                    );
                    if (matches > 0) {
                        searchedItem.addToMatchedKeys(term);
                        searchedItem.incMatches(matches);
                    } else {
                        searchedItem.addToMismatchedKeys(term);
                    }
                }
                if (searchedItem.getMatches() > 0) {
                    foundItems.add(searchedItem);
                }
            }
        }
        return foundItems.stream().sorted().limit(limit).collect(Collectors.toList());
    }

    private static void verifyNotBlank(String str) {
        if (StringUtils.isBlank(str)) {
            throw new IllegalArgumentException(String.format("String should not be empty or blank: '%s'", str));
        }
    }

    private static void verifyLegalLimit(int limit, int maxValue) {
        if (limit < 1 || limit > maxValue) {
            throw new IllegalArgumentException(String.format("Limit should not be less then 1 and greater then '%d'", maxValue));
        }
    }

}
