package com.ivashchyk.simplesearchengine.dao;

import java.util.Set;

import com.ivashchyk.simplesearchengine.api.model.Document;

public interface DocumentDoaAdapter {

    String create(Document document);

    boolean delete(String name);

    Document update(String name, Document document);

    Document find(String name);

    Set<String> getAllFileNames();

}
