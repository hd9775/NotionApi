package com.notion.api.database;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;

public class JsonBuilder {

    private String databaseId = "";

    public String jsonBuilder(String entry, String summary, String tag, String methodType, String parameterName, JsonNode responseNode) {
        // Create an ObjectMapper to build JSON structure
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode mainObject = objectMapper.createObjectNode();

        String json = "";

        try {
            // Create the parent JSON object
            ObjectNode parentObject = objectMapper.createObjectNode();

            parentObject.put("database_id", databaseId);

            ObjectNode childrenObject = objectMapper.createObjectNode();

            // Create the children array
            ArrayNode childrenArray = objectMapper.createArrayNode();

            // Create and add child objects to the children array
            ObjectNode requestBlock = objectMapper.createObjectNode();
            ObjectNode requestHeading = objectMapper.createObjectNode();
            ArrayNode requestRichText = objectMapper.createArrayNode();
            ObjectNode requestText = objectMapper.createObjectNode();
            requestText.put("text", objectMapper.createObjectNode().put("content", "Request"));
            requestRichText.add(requestText);
            requestHeading.set("rich_text", requestRichText);
            requestHeading.put("color", "blue");
            requestBlock.put("object", "block");
            requestBlock.set("heading_2", requestHeading);
            childrenArray.add(requestBlock);

            ObjectNode requestCodeBlock = objectMapper.createObjectNode();
            ObjectNode requestCode = objectMapper.createObjectNode();
            requestText = objectMapper.createObjectNode();
            requestText.put("content", parameterName);
            ArrayNode requestRichTextForCode = objectMapper.createArrayNode();
            ObjectNode richTextElement = objectMapper.createObjectNode();
            richTextElement.put("type", "text");
            richTextElement.set("text", requestText);
            requestRichTextForCode.add(richTextElement);
            requestCode.put("caption", objectMapper.createArrayNode());
            requestCode.put("rich_text", requestRichTextForCode);
            requestCode.put("language", "json");
            requestCode.putArray("caption");
            requestCodeBlock.put("object", "block");
            requestCodeBlock.set("code", requestCode);
            childrenArray.add(requestCodeBlock);

            ObjectNode responseBlock = objectMapper.createObjectNode();
            ObjectNode responseHeading = objectMapper.createObjectNode();
            ArrayNode responseRichText = objectMapper.createArrayNode();
            ObjectNode responseText = objectMapper.createObjectNode();
            responseText.put("text", objectMapper.createObjectNode().put("content", "Request"));
            responseRichText.add(responseText);
            responseHeading.set("rich_text", responseRichText);
            responseHeading.put("color", "blue");
            responseBlock.put("object", "block");
            responseBlock.set("heading_2", responseHeading);
            childrenArray.add(responseBlock);

            ObjectNode responseCodeBlock = objectMapper.createObjectNode();
            ObjectNode responseCode = objectMapper.createObjectNode();
            responseText = objectMapper.createObjectNode();
            responseText.put("content", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseNode));
            ArrayNode responseRichTextForCode = objectMapper.createArrayNode();
            richTextElement = objectMapper.createObjectNode();
            richTextElement.put("type", "text");
            richTextElement.set("text", responseText);
            responseRichTextForCode.add(richTextElement);
            responseCode.put("rich_text", responseRichTextForCode);
            responseCode.put("language", "json");
            responseCode.putArray("caption");
            responseCodeBlock.put("object", "block");
            responseCodeBlock.set("code", responseCode);
            childrenArray.add(responseCodeBlock);

            childrenObject.set("children", childrenArray);

            // Create the properties object
            ObjectNode propertiesObject = objectMapper.createObjectNode();

            // Entry Point property
            ObjectNode entryPoint = objectMapper.createObjectNode();
            ArrayNode entryPointTitleArray = objectMapper.createArrayNode();
            ObjectNode entryPointTitle = objectMapper.createObjectNode();
            ObjectNode entryPointTitleTextContent = objectMapper.createObjectNode();
            entryPointTitleTextContent.put("content", entry);
            entryPointTitle.put("type", "text");
            entryPointTitle.put("text", entryPointTitleTextContent);
            entryPoint.put("type", "title");
            entryPointTitleArray.add(entryPointTitle);
            entryPoint.set("title", entryPointTitleArray);
            propertiesObject.set("Entry Point", entryPoint);

            // Description property
            ObjectNode description = objectMapper.createObjectNode();
            ObjectNode descriptionRichText = objectMapper.createObjectNode();
            ArrayNode descriptionRichTextArray = objectMapper.createArrayNode();
            ObjectNode descriptionRichTextElement = objectMapper.createObjectNode();
            descriptionRichTextElement.put("content", summary);
            descriptionRichText.put("type", "text");
            descriptionRichText.set("text", descriptionRichTextElement);
            descriptionRichTextArray.add(descriptionRichText);
            description.put("type", "rich_text");
            description.set("rich_text", descriptionRichTextArray);
            propertiesObject.set("Description", description);

            // Type property
            ObjectNode type = objectMapper.createObjectNode();
            ObjectNode select = objectMapper.createObjectNode();
            select.put("name", tag);
            type.put("type", "select");
            type.set("select", select);
            propertiesObject.set("Type", type);

            // Method property
            ObjectNode method = objectMapper.createObjectNode();
            ObjectNode selectMethod = objectMapper.createObjectNode();
            selectMethod.put("name", methodType);
            method.put("type", "select");
            method.set("select", selectMethod);
            propertiesObject.set("Method", method);

            // Role property
            ObjectNode role = objectMapper.createObjectNode();
            ObjectNode selectRole = objectMapper.createObjectNode();
            selectRole.put("name", "User");
            role.put("type", "select");
            role.set("select", selectRole);
            propertiesObject.set("Role", role);

            // Create the final JSON structure
            mainObject.set("parent", parentObject);
            mainObject.set("children", childrenArray);
            mainObject.set("properties", propertiesObject);

            // Convert the JSON structure to a JSON string
            json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(mainObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }
}

