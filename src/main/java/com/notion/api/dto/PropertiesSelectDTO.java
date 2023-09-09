package com.notion.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PropertiesSelectDTO {
    @JsonProperty("type")
    private String type;

    @JsonProperty("select")
    private SelectContent selectContent;

    public PropertiesSelectDTO(String content) {
        this.type = "select";
        this.selectContent = new SelectContent(content);
    }

    public static class SelectContent {
        @JsonProperty("name")
        private String name;

        public SelectContent(String content) {
            this.name = content;
        }
    }
}