package com.ivashchyk.simplesearchengine.api;

import com.ivashchyk.simplesearchengine.api.model.Document;

public interface DocumentApi {

    String create(Document document);

    boolean delete(String name);

    Document update(String name, Document document);

    Document find(String name);

}
