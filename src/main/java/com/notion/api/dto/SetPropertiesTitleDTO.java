package com.notion.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SetPropertiesTitleDTO {
    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private String type;

    @JsonProperty("title")
    private TitleContent titleContent;

    public SetPropertiesTitleDTO(String name) {
        this.name = name;
        this.type = "title";
        this.titleContent = new TitleContent();
    }

    public static class TitleContent {
        public TitleContent() {
        }
    }
}
