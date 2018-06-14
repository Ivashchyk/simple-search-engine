package com.ivashchyk.simplesearchengine.dao;

import java.util.Set;

import javax.inject.Inject;

import com.ivashchyk.simplesearchengine.api.model.Document;

import lombok.NonNull;

public class DocumentDao {

    private final DocumentDoaAdapter adapter;

    @Inject
    public DocumentDao(@NonNull DocumentDoaAdapter adapter) {
        this.adapter = adapter;
    }

    public String create(@NonNull Document document) {
        return adapter.create(document);
    }

    public boolean delete(@NonNull String name) {
        return adapter.delete(name);
    }

    public Document update(@NonNull String name, @NonNull Document document) {
        return adapter.update(name, document);
    }

    public Document find(@NonNull String name) {
        return adapter.find(name);
    }

    public Set<String> getAllFileNames() {
        return adapter.getAllFileNames();
    }

}
