package com.notion.api.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.notion.api.SwaggerParser;
import com.notion.api.dto.*;

import java.util.List;

public class JsonBuilder {

    public String createData(String requestCode, String responseCode, String entry, String summary,
                       String type, String method, String role) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode mainObject = objectMapper.createObjectNode();

        // parentObject
        ObjectNode parentObject = objectMapper.createObjectNode();
        parentObject.put("database_id", SwaggerParser.DATABASE_ID);

        mainObject.set("parent", parentObject);

        // childrenObject
        ArrayNode childrenArray = objectMapper.createArrayNode();

        childrenArray.add(objectMapper.valueToTree(new BlockHeading2DTO("Request", "blue")));
        childrenArray.add(objectMapper.valueToTree(new BlockCodeDTO(requestCode, "json")));
        childrenArray.add(objectMapper.valueToTree(new BlockHeading2DTO("Response", "blue")));
        childrenArray.add(objectMapper.valueToTree(new BlockCodeDTO(responseCode, "json")));

        mainObject.set("children", childrenArray);

        // properties object
        ObjectNode propertiesObject = objectMapper.createObjectNode();

        propertiesObject.set("Entry Point", objectMapper.valueToTree(new PropertiesTitleDTO(entry)));
        propertiesObject.set("Description", objectMapper.valueToTree(new PropertiesRichTextDTO(summary)));
        propertiesObject.set("Type", objectMapper.valueToTree(new PropertiesSelectDTO(type)));
        propertiesObject.set("Method", objectMapper.valueToTree(new PropertiesSelectDTO(method)));
        propertiesObject.set("Role", objectMapper.valueToTree(new PropertiesSelectDTO(role)));

        mainObject.set("properties", propertiesObject);


        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(mainObject);
    }

    public String createDatabase(String pageId, String emoji, String title) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode mainObject = objectMapper.createObjectNode();

        // parentObject
        ObjectNode parentObject = objectMapper.createObjectNode();
        parentObject.put("type", "page_id");
        parentObject.put("page_id", pageId);
        mainObject.set("parent", parentObject);

        // iconObject
        ObjectNode iconObject = objectMapper.createObjectNode();
        iconObject.put("type", "emoji");
        iconObject.put("emoji", emoji);
        mainObject.set("icon", iconObject);

        // titleObject
        ObjectNode titleObject = objectMapper.createObjectNode();
        ArrayNode titleArray = objectMapper.createArrayNode();
        titleArray.add(objectMapper.valueToTree(new SetTitleDTO("text", title)));
        titleObject.set("title", titleArray);
        mainObject.set("title", titleObject);

        // properties object
        ObjectNode propertiesObject = objectMapper.createObjectNode();
        ObjectNode roleObject = objectMapper.createObjectNode();
        List<SetPropertiesSelectDTO.SelectContentDTO.OptionDTO> roleOptions = List.of(
                new SetPropertiesSelectDTO.SelectContentDTO.OptionDTO("Admin", "red"),
                new SetPropertiesSelectDTO.SelectContentDTO.OptionDTO("User", "yellow")
        );
        roleObject.set("Role", objectMapper.valueToTree((new SetPropertiesSelectDTO("Role", roleOptions))));
        propertiesObject.set("Role", roleObject);

        ObjectNode methodObject = objectMapper.createObjectNode();
        List<SetPropertiesSelectDTO.SelectContentDTO.OptionDTO> methodOtions = List.of(
                new SetPropertiesSelectDTO.SelectContentDTO.OptionDTO("GET", "green"),
                new SetPropertiesSelectDTO.SelectContentDTO.OptionDTO("POST", "blue"),
                new SetPropertiesSelectDTO.SelectContentDTO.OptionDTO("PUT", "pink"),
                new SetPropertiesSelectDTO.SelectContentDTO.OptionDTO("PATCH", "purple"),
                new SetPropertiesSelectDTO.SelectContentDTO.OptionDTO("DELETE", "red")
        );
        methodObject.set("Method", objectMapper.valueToTree((new SetPropertiesSelectDTO("Method", methodOtions))));
        propertiesObject.set("Method", methodObject);

        // descriptionObject
        propertiesObject.set("Description", objectMapper.valueToTree(new SetPropertiesRichTextDTO("")));

        // typeObject
        ObjectNode typeObject = objectMapper.createObjectNode();
        List<SetPropertiesSelectDTO.SelectContentDTO.OptionDTO> typeOptions = List.of(
                new SetPropertiesSelectDTO.SelectContentDTO.OptionDTO("ACCOUNT", "purple"),
                new SetPropertiesSelectDTO.SelectContentDTO.OptionDTO("USER", "green")
        );
        typeObject.set("Type", objectMapper.valueToTree((new SetPropertiesSelectDTO("Type", typeOptions))));
        propertiesObject.set("Type", typeObject);

        // entryPointObject
        propertiesObject.set("Entry Point", objectMapper.valueToTree(new SetPropertiesTitleDTO("Entry Point")));

        mainObject.set("properties", propertiesObject);

        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(mainObject);
    }

}
