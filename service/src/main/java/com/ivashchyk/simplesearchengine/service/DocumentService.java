package com.ivashchyk.simplesearchengine.service;

import javax.inject.Inject;

import com.ivashchyk.simplesearchengine.api.DocumentApi;
import com.ivashchyk.simplesearchengine.api.model.Document;
import com.ivashchyk.simplesearchengine.dao.DocumentDao;

import lombok.NonNull;

public class DocumentService implements DocumentApi {

    private final DocumentDao dao;

    @Inject
    public DocumentService(DocumentDao dao) {
        this.dao = dao;
    }

    public String create(@NonNull Document document) {
        return dao.create(document);
    }

    public boolean delete(@NonNull String name) {
        return dao.delete(name);
    }

    public Document update(@NonNull String name, @NonNull Document document) {
        return dao.update(name, document);
    }

    public Document find(@NonNull String name) {
        return dao.find(name);
    }
}
