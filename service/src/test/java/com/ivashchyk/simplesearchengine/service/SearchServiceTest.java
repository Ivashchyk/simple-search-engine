package com.ivashchyk.simplesearchengine.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ivashchyk.simplesearchengine.api.model.SearchedItem;
import org.junit.After;
import org.junit.Test;

import com.ivashchyk.simplesearchengine.api.model.Document;
import com.ivashchyk.simplesearchengine.api.model.SearchLine;
import com.ivashchyk.simplesearchengine.dao.DocumentDao;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SearchServiceTest {

    private SearchService searchService;

    private DocumentDao documentDao = mock(DocumentDao.class);

    @After
    public void tearDown() {
        reset(documentDao);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateSearchServiceWithNullDocumentDao() {
        new SearchService(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateSearchServiceWithMaxLimitLessThanOne() {
        new SearchService(documentDao, 0);
    }

    @Test
    public void testSearchStrongWithEmptySearchLine() {
        searchService = new SearchService(documentDao, 50);
        try {
            searchService.searchStrong(new SearchLine(""), 5);
            fail("Should be unreachable");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertNotNull(e.getMessage());
            assertThat(e.getMessage(), containsString("String should not be empty or blank"));
            verify(documentDao, never()).getAllFileNames();
        }
    }

    @Test
    public void testSearchStrongWithNotLegalLimit() {
        int maxLimit = 50;
        searchService = new SearchService(documentDao, maxLimit);
        try {
            searchService.searchStrong(new SearchLine("test search"), -5);
            fail("Should be unreachable");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertNotNull(e.getMessage());
            assertThat(e.getMessage(), containsString(String.format("Limit should not be less then 1 and greater then '%d'", maxLimit)));
            verify(documentDao, never()).getAllFileNames();
        }
    }

    @Test
    public void testSearchStrongWithNoDocuments() {
        searchService = new SearchService(documentDao, 50);
        when(documentDao.getAllFileNames()).thenReturn(Collections.emptySet());
        List<Document> result = searchService.searchStrong(new SearchLine("test search"));
        assertNotNull(result);
        assertThat(result.size(), is(0));
        verify(documentDao).getAllFileNames();
        verify(documentDao, never()).find(any());
    }

    @Test
    public void testSearchStrongLegalCase() {
        searchService = new SearchService(documentDao, 50);
        Set<String> fileNames = Stream.of("file1.txt", "file2.txt", "file3.txt").collect(Collectors.toSet());
        when(documentDao.getAllFileNames()).thenReturn(fileNames);
        when(documentDao.find(anyString()))
            .thenAnswer(invocation ->
                Document.builder()
                    .name((String) invocation.getArguments()[0])
                    .content("some test search blabla")
                    .build())
            .thenAnswer(invocation ->
                Document.builder()
                    .name((String) invocation.getArguments()[0])
                    .content("some test search 111")
                    .build())
            .thenAnswer(invocation ->
                Document.builder()
                    .name((String) invocation.getArguments()[0])
                    .content("not matched search")
                    .build());
        List<Document> result = searchService.searchStrong(new SearchLine("test search"));
        assertNotNull(result);
        assertThat(result.size(), is(2));
        verify(documentDao).getAllFileNames();
        verify(documentDao, times(3)).find(anyString());
    }

    @Test
    public void testSearchStrongWithLimit() {
        searchService = new SearchService(documentDao, 50);
        Set<String> fileNames = Stream.generate(() -> UUID.randomUUID().toString()).limit(10).collect(Collectors.toSet());
        when(documentDao.getAllFileNames()).thenReturn(fileNames);
        fileNames.forEach(fileName ->
            when(documentDao.find(fileName))
                .thenReturn(Document.builder()
                    .name(fileName)
                    .content(UUID.randomUUID().toString() + " some test search blabla")
                    .build())
        );
        List<Document> result = searchService.searchStrong(new SearchLine("test search"), 5);
        assertNotNull(result);
        assertThat(result.size(), is(5));
        verify(documentDao).getAllFileNames();
        verify(documentDao, times(5)).find(anyString());
    }

    @Test
    public void testSearchSoftWithEmptySearchLine() {
        searchService = new SearchService(documentDao, 50);
        try {
            searchService.searchSoft(new SearchLine(""), 5);
            fail("Should be unreachable");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertNotNull(e.getMessage());
            assertThat(e.getMessage(), containsString("String should not be empty or blank"));
            verify(documentDao, never()).getAllFileNames();
        }
    }

    @Test
    public void testSearchSoftWithNotLegalLimit() {
        int maxLimit = 50;
        searchService = new SearchService(documentDao, maxLimit);
        try {
            searchService.searchSoft(new SearchLine("test search"), -5);
            fail("Should be unreachable");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertNotNull(e.getMessage());
            assertThat(e.getMessage(), containsString(String.format("Limit should not be less then 1 and greater then '%d'", maxLimit)));
            verify(documentDao, never()).getAllFileNames();
        }
    }

    @Test
    public void testSearchSoftLegalCase() {
        searchService = new SearchService(documentDao, 50);
        Set<String> fileNames = Stream.of("file1.txt", "file2.txt", "file3.txt").collect(Collectors.toSet());
        when(documentDao.getAllFileNames()).thenReturn(fileNames);
        when(documentDao.find(anyString()))
            .thenAnswer(invocation ->
                Document.builder()
                    .name((String) invocation.getArguments()[0])
                    .content("some test search blabla")
                    .build())
            .thenAnswer(invocation ->
                Document.builder()
                    .name((String) invocation.getArguments()[0])
                    .content("some test search 111 search search")
                    .build())
            .thenAnswer(invocation ->
                Document.builder()
                    .name((String) invocation.getArguments()[0])
                    .content("not matched")
                    .build());
        List<SearchedItem> result = searchService.searchSoft(new SearchLine("test search"));
        assertNotNull(result);
        assertThat(result.size(), is(2));
        SearchedItem previous = null;
        int assertionCount = 0;
        for (SearchedItem current : result) {
            if (previous != null) {
                assertTrue(previous.getMatches() >=current.getMatches());
                assertionCount++;
            }
            previous = current;
        }
        assertThat(assertionCount, is(1));
        verify(documentDao).getAllFileNames();
        verify(documentDao, times(3)).find(anyString());
    }

    @Test
    public void testSearchSoftWithSameMatchedButDifferentMismatched() {
        searchService = new SearchService(documentDao, 50);
        Set<String> fileNames = Stream.of("file1.txt", "file2.txt").collect(Collectors.toSet());
        when(documentDao.getAllFileNames()).thenReturn(fileNames);
        when(documentDao.find(anyString()))
            .thenAnswer(invocation ->
                Document.builder()
                    .name((String) invocation.getArguments()[0])
                    .content("some test search blabla")
                    .build())
            .thenAnswer(invocation ->
                Document.builder()
                    .name((String) invocation.getArguments()[0])
                    .content("search search search")
                    .build())
            .thenAnswer(invocation ->
                Document.builder()
                    .name((String) invocation.getArguments()[0])
                    .content("not matched")
                    .build());
        List<SearchedItem> result = searchService.searchSoft(new SearchLine("test search blabla"));
        assertNotNull(result);
        assertThat(result.size(), is(2));
        SearchedItem previous = null;
        int assertionCount = 0;
        for (SearchedItem current : result) {
            if (previous != null) {
                assertThat(previous.getMatches(), is(current.getMatches()));
                assertTrue(previous.getMismatchedKeys().size() < current.getMismatchedKeys().size());
                assertionCount++;
            }
            previous = current;
        }
        assertThat(assertionCount, is(1));
        verify(documentDao).getAllFileNames();
        verify(documentDao, times(2)).find(anyString());
    }

    @Test
    public void testSearchSoftWithLimit() {
        searchService = new SearchService(documentDao, 50);
        Set<String> fileNames = Stream.generate(() -> UUID.randomUUID().toString()).limit(10).collect(Collectors.toSet());
        when(documentDao.getAllFileNames()).thenReturn(fileNames);
        fileNames.forEach(fileName ->
            when(documentDao.find(fileName))
                .thenReturn(Document.builder()
                    .name(fileName)
                    .content(UUID.randomUUID().toString() + " some test search blabla")
                    .build())
        );
        List<SearchedItem> result = searchService.searchSoft(new SearchLine("test search"), 5);
        assertNotNull(result);
        assertThat(result.size(), is(5));
        verify(documentDao).getAllFileNames();
        verify(documentDao, times(10)).find(anyString());
    }

}
