package com.ivashchyk.simplesearchengine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ivashchyk.simplesearchengine.api.DocumentApi;
import com.ivashchyk.simplesearchengine.api.model.Document;

@Configuration
public class SpringConfig {

    @Bean
    public DocumentApi documentApi() {
        return new DocumentApi() {

            @Override
            public String create(Document document) {
                return null;
            }

            @Override
            public boolean delete(String name) {
                return false;
            }

            @Override
            public Document update(String name, Document document) {
                return null;
            }

            @Override
            public Document find(String name) {
                return null;
            }
        };
    }

}
