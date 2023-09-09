package com.notion.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class BlockCodeDTO {
    @JsonProperty("object")
    private String object;

    @JsonProperty("code")
    private Code code;

    public BlockCodeDTO(String content, String language) {
        this.object = "block";
        this.code = new Code(content, language);
    }

    public static class Code {
        @JsonProperty("caption")
        private List<String> caption;

        @JsonProperty("rich_text")
        private List<RichText> richText;

        @JsonProperty("language")
        private String language;

        public Code(String content, String language) {
            this.caption = List.of();
            this.richText = List.of(new RichText(content));
            this.language = language;
        }
    }

    public static class RichText {
        @JsonProperty("type")
        private String type;

        @JsonProperty("text")
        private Text text;

        public RichText(String content) {
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
