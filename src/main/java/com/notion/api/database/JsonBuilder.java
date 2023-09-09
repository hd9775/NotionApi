package com.notion.api.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.notion.api.SwaggerParser;
import com.notion.api.dto.*;

public class JsonBuilder {

    public String create(String requestCode, String responseCode, String entry, String summary,
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



}
