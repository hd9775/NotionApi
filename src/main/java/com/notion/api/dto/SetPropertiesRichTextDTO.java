package com.notion.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SetPropertiesRichTextDTO {
    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private String type;

    @JsonProperty("rich_text")
    private RichTextContent richTextContent;

    public SetPropertiesRichTextDTO(String name) {
        this.name = name;
        this.type = "rich_text";
        this.richTextContent = new RichTextContent();
    }

    public static class RichTextContent {

        public RichTextContent() {
        }
    }
}
