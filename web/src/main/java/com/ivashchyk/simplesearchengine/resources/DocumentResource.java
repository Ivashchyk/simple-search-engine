package com.ivashchyk.simplesearchengine.resources;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ivashchyk.simplesearchengine.api.DocumentApi;
import com.ivashchyk.simplesearchengine.api.model.Document;

@Controller
@RequestMapping("test")
public class DocumentResource {

    private final DocumentApi documentApi;

    @Autowired
    public DocumentResource(DocumentApi documentApi) {
        this.documentApi = documentApi;
    }

    @PostMapping("/cread")
    @ResponseBody
    public String createDocument(@NotNull Document document) {
        if (StringUtils.isEmpty(document.getName())) {
            document.setName(UUID.randomUUID().toString() + ".txt");
        }
        return documentApi.create(document);
    }

    @GetMapping("/test-resource")
    @ResponseBody
    public String testResource() {
        return "triggered";
    }

}
