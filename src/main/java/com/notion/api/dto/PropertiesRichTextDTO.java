package com.notion.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PropertiesRichTextDTO {
    @JsonProperty("type")
    private String type;

    @JsonProperty("rich_text")
    private List<RichTextContent> richTextContent;

    public PropertiesRichTextDTO(String content) {
        this.type = "rich_text";
        this.richTextContent = List.of(new RichTextContent(content));
    }

    public static class RichTextContent {
        @JsonProperty("type")
        private String type;

        @JsonProperty("text")
        private Text text;

        public RichTextContent(String content) {
            this.type = "text";
            this.text = new Text(content);
        }
    }

    public static class Text {
        @JsonProperty("content")
        private String content;

        public Text(String content) {
            this.content = content;
        }
    }
}
