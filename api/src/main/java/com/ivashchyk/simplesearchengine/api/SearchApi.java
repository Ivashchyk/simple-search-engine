package com.ivashchyk.simplesearchengine.api;

import java.util.List;

import com.ivashchyk.simplesearchengine.api.model.Document;
import com.ivashchyk.simplesearchengine.api.model.SearchLine;
import com.ivashchyk.simplesearchengine.api.model.SearchedItem;

public interface SearchApi {

    int DEFAULT_SEARCH_LIMIT = 10;

    List<Document> searchStrong(SearchLine searchLine);

    List<Document> searchStrong(SearchLine searchLine, int limit);

    List<SearchedItem> searchSoft(SearchLine searchLine);

    List<SearchedItem> searchSoft(SearchLine searchLine, int limit);
}
