package com.ivashchyk.simplesearchengine.api.model;

import javax.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Document {

    @NotBlank
    private String name;

    private String content;

}
