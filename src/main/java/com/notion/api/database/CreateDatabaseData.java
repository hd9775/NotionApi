package com.notion.api.database;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.notion.api.Request;

import java.io.IOException;
import java.net.HttpURLConnection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CreateDatabaseData {
    String url = "https://api.notion.com/v1/pages";
    String urlMethod = "POST";
    JsonBuilder jsonBuilder = new JsonBuilder();
    ObjectMapper objectMapper = new ObjectMapper();
    Request request = new Request();

    public void create(String json) throws IOException {
        JsonNode rootNode = objectMapper.readTree(json);

        JsonNode pathsNode = rootNode.path("paths");

        Map<String, JsonNode> definitionMap = new HashMap<>();
        JsonNode definitionsNode = rootNode.path("definitions");
        Iterator<String> definitionsNames = definitionsNode.fieldNames();
        while (definitionsNames.hasNext()) {
            String entry = definitionsNames.next();
            JsonNode entryNode = definitionsNode.path(entry).path("properties");
            definitionMap.put(entry, entryNode);
        }

        Iterator<String> fieldNames = pathsNode.fieldNames();
        // API 경로
        while (fieldNames.hasNext()) {
            String entry = fieldNames.next();
            JsonNode entryNode = pathsNode.path(entry);
            Iterator<String> methodNames = entryNode.fieldNames();
            // Method 종류
            while (methodNames.hasNext()) {
                String method = methodNames.next();
                switch (method) {
                    case "get":
                        method("GET", entry, entryNode.path(method), definitionMap);
                        break;
                    case "post":
                        method("POST", entry, entryNode.path(method), definitionMap);
                        break;
                    case "put":
                        method("PUT", entry, entryNode.path(method), definitionMap);
                        break;
                    case "delete":
                        method("DELETE", entry, entryNode.path(method), definitionMap);
                        break;
                    case "patch":
                        method("PATCH", entry, entryNode.path(method), definitionMap);
                        break;
                    default:
                        System.out.println("알 수 없는 메소드입니다.");
                        break;
                }
            }

        }
    }

    public void method(String method, String entry, JsonNode methodNode, Map<String, JsonNode> definitionMap) throws IOException {
        JsonNode summaryNode = methodNode.path("summary");
        JsonNode tagsNode = methodNode.path("tags");
        JsonNode parametersNode = methodNode.path("parameters");
        JsonNode responseNode = checkResponseNode(methodNode.path("responses"), definitionMap);

        String summary = summaryNode.asText();
        String tag = tagsNode.get(0).asText().split("-controller")[0];
        Iterator<JsonNode> parameters = parametersNode.elements();
        StringBuilder parameterName = new StringBuilder();
        while(parameters.hasNext()) {
            JsonNode parameter = parameters.next();
            String param = parameter.path("name").asText();
            if(definitionMap.containsKey(param.substring(0, 1).toUpperCase() + param.substring(1))) {
                parameterName.append(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(definitionMap.get(param.substring(0, 1).toUpperCase() + param.substring(1))));
            } else {
                parameterName.append(parameter.path("name").asText());
            }
            if(parameters.hasNext()) {
                parameterName.append("\n");
            }
        }

        String json = jsonBuilder.createData(parameterName.toString(), objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseNode), entry, summary, tag.toUpperCase(), method, "USER");

        HttpURLConnection con = request.sendRequest(url, urlMethod, json);

        // 응답 코드 확인
        System.out.println("데이터 추가 요청에 대항 응답 코드: " + con.getResponseCode() + " -> " + method + "\t" + entry);
    }

    public ObjectNode checkResponseNode(JsonNode responseNode, Map<String, JsonNode> definitionMap) {
        Iterator<String> responseNames = responseNode.fieldNames();

        ObjectNode modifiedResponseNode = JsonNodeFactory.instance.objectNode();

        while(responseNames.hasNext()) {
            String responseName = responseNames.next();
            if(checkDefaultResponseCode(responseNode.path(responseName).path("description").asText())) {
                continue;
            }

            JsonNode response = responseNode.path(responseName).path("schema").path("items").path("$ref");
            JsonNode response2 = responseNode.path(responseName).path("schema").path("$ref");

            String[] responseRef = response.asText().split("/");
            String[] response2Ref = response2.asText().split("/");
            if(responseRef.length == 3 && definitionMap.containsKey(responseRef[2])) {
                modifiedResponseNode.set(responseName, checkResponseDTONode(definitionMap.get(responseRef[2]), definitionMap));
            } else if(response2Ref.length == 3 && definitionMap.containsKey(response2Ref[2])) {
                modifiedResponseNode.set(responseName, checkResponseDTONode(definitionMap.get(response2Ref[2]), definitionMap));
            } else {
                modifiedResponseNode.set(responseName, responseNode.path(responseName));
            }
        }

        return modifiedResponseNode;
    }

    public JsonNode checkResponseDTONode(JsonNode responseDTONode, Map<String, JsonNode> definitionMap) {
        Iterator<String> responseDTONames = responseDTONode.fieldNames();

        ObjectNode modifiedResponseDTONode = JsonNodeFactory.instance.objectNode();

        while(responseDTONames.hasNext()) {
            String responseName = responseDTONames.next();
            JsonNode response = responseDTONode.path(responseName).path("$ref");
            String[] tmp = response.asText().split("/");
            if(tmp.length == 3 && definitionMap.containsKey(tmp[2])) {
                String responseRef = tmp[2];
                modifiedResponseDTONode.set(responseName, definitionMap.get(responseRef));
            } else {
                modifiedResponseDTONode.set(responseName, responseDTONode.path(responseName));
            }
        }

        return modifiedResponseDTONode;
    }

    public boolean checkDefaultResponseCode(String description) {
        if(description.equals("Created")) {
            return true;
        } else if(description.equals("No Content")) {
            return true;
        } else if(description.equals("OK")) {
            return true;
        } else if(description.equals("Bad Request")) {
            return true;
        } else if(description.equals("Unauthorized")) {
            return true;
        } else if(description.equals("Forbidden")) {
            return true;
        } else if(description.equals("Not Found")) {
            return true;
        } else if(description.equals("Conflict")) {
            return true;
        } else if(description.equals("Unprocessable Entity")) {
            return true;
        } else if(description.equals("Internal Server Error")) {
            return true;
        } else {
            return false;
        }
    }

}
