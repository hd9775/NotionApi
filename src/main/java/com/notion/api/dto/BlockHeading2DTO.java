package com.notion.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BlockHeading2DTO {
    @JsonProperty("object")
    private String object;

    @JsonProperty("heading_2")
    private Heading2 heading2;

    public BlockHeading2DTO(String content, String color) {
        this.object = "block";
        this.heading2 = new Heading2(content, color);
    }

    public static class Heading2 {
        @JsonProperty("rich_text")
        private List<RichText> richText;

        @JsonProperty("color")
        private String color;

        public Heading2(String content, String color) {
            this.richText = List.of(new RichText(content));
            this.color = color;
        }
    }

    public static class RichText {
        @JsonProperty("text")
        private Text text;

        public RichText(String content) {
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
