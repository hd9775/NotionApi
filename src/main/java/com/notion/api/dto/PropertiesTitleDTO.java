package com.notion.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PropertiesTitleDTO {
    @JsonProperty("type")
    private String type;

    @JsonProperty("title")
    private List<Title> title;

    public PropertiesTitleDTO(String content) {
        this.type = "title";
        this.title = List.of(new Title(content));
    }

    public static class Title {
        @JsonProperty("type")
        private String type;

        @JsonProperty("text")
        private TitleText titleText;

        public Title(String content) {
            this.type = "text";
            this.titleText = new TitleText(content);
        }
    }

    public static class TitleText {
        @JsonProperty("content")
        private String content;

        public TitleText(String content) {
            this.content = content;
        }
    }
}
