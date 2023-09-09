package com.notion.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SetPropertiesSelectDTO {
    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private String type;

    @JsonProperty("select")
    private SelectContentDTO selectContent;

    public SetPropertiesSelectDTO(String name, List<SelectContentDTO.OptionDTO> options) {
        this.name = name;
        this.type = "select";
        this.selectContent = new SelectContentDTO(options);
    }

    public static class SelectContentDTO {
        @JsonProperty("options")
        private List<OptionDTO> options;

        public SelectContentDTO(List<OptionDTO> options) {
            this.options = options;
        }

        public static class OptionDTO {
            @JsonProperty("name")
            private String name;

            @JsonProperty("color")
            private String color;

            public OptionDTO(String name, String color) {
                this.name = name;
                this.color = color;
            }
        }
    }
}
