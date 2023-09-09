package com.notion.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SetTitleDTO {
    @JsonProperty("type")
    private String type;

    @JsonProperty("text")
    private TextDTO text;

    public SetTitleDTO(String type, String content) {
        this.type = type;
        this.text = new TextDTO(content);
    }

    public class TextDTO {
        @JsonProperty("content")
        private String content;

        @JsonProperty("link")
        private String link;

        public TextDTO(String content) {
            this.content = content;
            this.link = null;
        }
    }
}
