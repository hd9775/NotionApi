package com.notion.api.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.notion.api.Request;

import java.io.IOException;
import java.net.HttpURLConnection;

import java.util.*;

public class CreateDatabaseData {
    String url = "https://api.notion.com/v1/pages";
    String urlMethod = "POST";
    JsonBuilder jsonBuilder = new JsonBuilder();
    ObjectMapper objectMapper = new ObjectMapper();
    Request request = new Request();

    public Map<Integer, Integer> create(String json) throws IOException {
        JsonNode rootNode = objectMapper.readTree(json);

        JsonNode pathsNode = rootNode.path("paths");

        Map<String, JsonNode> definitionMap = new HashMap<>();
        JsonNode definitionsNode = rootNode.path("definitions");
        JsonNode definitionsNode2 = rootNode.path("components").path("schemas");
        Iterator<String> definitionsNames = definitionsNode.fieldNames();
        while (definitionsNames.hasNext()) {
            String entry = definitionsNames.next();
            JsonNode entryNode = definitionsNode.path(entry).path("properties");
            definitionMap.put(entry, entryNode);
        }
        Iterator<String> definitionsNames2 = definitionsNode2.fieldNames();
        while (definitionsNames2.hasNext()) {
            String entry = definitionsNames2.next();
            JsonNode entryNode = definitionsNode2.path(entry).path("properties");
            definitionMap.put(entry, entryNode);
        }

        Map<Integer, Integer> responseCodeMap = new HashMap<>();
        Iterator<String> fieldNames = pathsNode.fieldNames();
        // API 경로
        while (fieldNames.hasNext()) {
            String entry = fieldNames.next();
            JsonNode entryNode = pathsNode.path(entry);
            Iterator<String> methodNames = entryNode.fieldNames();

            // Method 종류
            while (methodNames.hasNext()) {
                String method = methodNames.next();

                int responseCode = 0;
                switch (method) {
                    case "get":
                        responseCode = method("GET", entry, entryNode.path(method), definitionMap);
                        break;
                    case "post":
                        responseCode = method("POST", entry, entryNode.path(method), definitionMap);
                        break;
                    case "put":
                        responseCode = method("PUT", entry, entryNode.path(method), definitionMap);
                        break;
                    case "delete":
                        responseCode = method("DELETE", entry, entryNode.path(method), definitionMap);
                        break;
                    case "patch":
                        responseCode = method("PATCH", entry, entryNode.path(method), definitionMap);
                        break;
                    default:
                        System.out.println("알 수 없는 메소드입니다.");
                        break;
                }

                if(responseCodeMap.containsKey(responseCode)) {
                    responseCodeMap.put(responseCode, responseCodeMap.get(responseCode) + 1);
                } else {
                    responseCodeMap.put(responseCode, 1);
                }
            }

        }

        return responseCodeMap;
    }

    public int method(String method, String entry, JsonNode methodNode, Map<String, JsonNode> definitionMap) throws IOException {
        JsonNode summaryNode = methodNode.path("summary");
        JsonNode roleNode = methodNode.path("description");
        String role = (roleNode.asText() == null || roleNode.asText().equals("")) ? "ALL" : roleNode.asText();
        JsonNode tagsNode = methodNode.path("tags");
        JsonNode parametersNode = methodNode.path("parameters");
        JsonNode requestBodyNode = methodNode.path("requestBody");
        String responseString = checkResponseNode(methodNode.path("responses"), definitionMap);

        String summary = summaryNode.asText();
        String tag = tagsNode.get(0).asText().split("-controller")[0].replace("-", "");
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

        String[] requestRef = requestBodyNode.path("content").path("application/json").path("schema").path("$ref").asText().split("/");

        if(requestRef.length == 4 && definitionMap.containsKey(requestRef[3])) {
            parameterName.append(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(definitionMap.get(requestRef[3])));
        }

        String json = jsonBuilder.createData(parameterName.toString(), responseString, entry, summary, tag.toUpperCase(), method, role);

        HttpURLConnection con = request.sendRequest(url, urlMethod, json);

        if(con.getResponseCode() != 200) {
            System.out.println(json);
            System.out.println(con.getResponseMessage());
        }
        // 응답 코드 확인
        System.out.println("데이터 추가 요청에 대항 응답 코드: " + con.getResponseCode() + " -> " + method + "\t" + entry);

        return con.getResponseCode();
    }

    public String checkResponseNode(JsonNode responseNode, Map<String, JsonNode> definitionMap) throws JsonProcessingException {
        StringBuilder responseString = new StringBuilder();

        Iterator<String> responseNames = responseNode.fieldNames();

        ObjectNode modifiedResponseNode = JsonNodeFactory.instance.objectNode();

        Set<String> responseSet = new HashSet<>();
        Map<String, String> responseCodeMap = new HashMap<>();

        while(responseNames.hasNext()) {
            String responseName = responseNames.next();
            String responseDescription = responseNode.path(responseName).path("description").asText();
            if(checkDefaultResponseCode(responseDescription)) {
                continue;
            }

            JsonNode response3 = responseNode.path(responseName).path("content").path("*/*").path("schema").path("$ref");
            JsonNode response4 = responseNode.path(responseName).path("content").path("*/*").path("schema").path("items").path("$ref");

            String[] response3Ref = response3.asText().split("/");
            String[] response4Ref = response4.asText().split("/");

            if(response3Ref.length == 4 && definitionMap.containsKey(response3Ref[3])) {
                responseSet.add(response3Ref[3]);
                responseCodeMap.put(responseName, responseDescription);
            } else if(response4Ref.length == 4 && definitionMap.containsKey(response4Ref[3])) {
                responseSet.add(response4Ref[3]);
                responseCodeMap.put(responseName, responseDescription);
                modifiedResponseNode.put(responseName, responseDescription);
            } else {
                modifiedResponseNode.put(responseName, responseDescription);
            }
        }

        Map<String, String> sortedMap = new TreeMap<>(responseCodeMap);
        sortedMap.forEach(modifiedResponseNode::put);

        responseString.append("Response Code\n");
        responseString.append(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(modifiedResponseNode));

        if(!responseSet.isEmpty()) {
            responseString.append("\n\nResponse Data\n");

            for(String responseRef : responseSet) {
                responseString.append(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(checkResponseDTONode(definitionMap.get(responseRef), definitionMap))+ "\n");
            }
        }
        return responseString.toString();
    }

    public JsonNode checkResponseDTONode(JsonNode responseDTONode, Map<String, JsonNode> definitionMap) {
        Iterator<String> responseDTONames = responseDTONode.fieldNames();

        ObjectNode modifiedResponseDTONode = JsonNodeFactory.instance.objectNode();

        while(responseDTONames.hasNext()) {
            String responseName = responseDTONames.next();
            JsonNode response = responseDTONode.path(responseName).path("$ref");
            String[] tmp = response.asText().split("/");
            if(tmp.length == 4 && definitionMap.containsKey(tmp[3])) {
                String responseRef = tmp[3];
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
