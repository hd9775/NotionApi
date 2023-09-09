package com.notion.api.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.notion.api.SwaggerParser;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

public class CreateDatabase {
    String url = "https://api.notion.com/v1/pages";

    JsonBuilder jsonBuilder = new JsonBuilder();
    ObjectMapper objectMapper = new ObjectMapper();

    public void sendRequest(String jsonBody, String entry, String methodType) {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // HTTP 요청 메소드 설정
            con.setRequestMethod("POST");

            // 요청 헤더 설정
            con.setRequestProperty("Authorization", SwaggerParser.AUTHORIZATION_Token);
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Notion-Version", "2022-06-28");
            con.setRequestProperty("Accept-Charset", "UTF-8");

            // POST 요청을 허용하도록 설정
            con.setDoOutput(true);

            // 요청 바디 작성 및 전송
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            wr.flush();
            wr.close();

            // 응답 코드 확인
            int responseCode = con.getResponseCode();
            System.out.println(entry + " " + methodType + "에 대한 " + "POST" + " 요청에 대한 응답 코드: " + responseCode);


        } catch (Exception ignored) {
        }
    }

    public void method(String method, String entry, JsonNode methodNode, Map<String, JsonNode> definitionMap) throws JsonProcessingException {
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

        String json = jsonBuilder.create(parameterName.toString(), objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseNode), entry, summary, tag.toUpperCase(), method, "USER");

        sendRequest(json, entry, method);
    }

    public ObjectNode checkResponseNode(JsonNode responseNode, Map<String, JsonNode> definitionMap) {
        Iterator<String> responseNames = responseNode.fieldNames();

        ObjectNode modifiedResponseNode = JsonNodeFactory.instance.objectNode();

        while(responseNames.hasNext()) {
            String responseName = responseNames.next();
            JsonNode response = responseNode.path(responseName).path("schema").path("items").path("$ref");
            if(response.asText().contains("DTO")) {
                String responseRef = response.asText().split("/")[2];
                modifiedResponseNode.set(responseName, checkResponseDTONode(definitionMap.get(responseRef), definitionMap));

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

}
