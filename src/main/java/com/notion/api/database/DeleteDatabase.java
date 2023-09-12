package com.notion.api.database;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.notion.api.Request;
import com.notion.api.SwaggerParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Iterator;

public class DeleteDatabase {
    String getUrl = "https://api.notion.com/v1/databases/" + SwaggerParser.DATABASE_ID + "/query";
    String getUrlMethod = "POST";


    String deleteUrl = "https://api.notion.com/v1/blocks/";
    String deleteUrlMethod = "DELETE";

    ObjectMapper objectMapper = new ObjectMapper();
    Request request = new Request();

    public void delete() throws IOException {
        JsonNode json = getDatabaseDate();

        JsonNode resultsNode = json.path("results");

        if (resultsNode != null && resultsNode.isArray()) {
            for (JsonNode result : resultsNode) {
                JsonNode idNode = result.get("id");
                if (idNode != null) {
                    String idValue = idNode.asText();
                    HttpURLConnection con = request.sendRequest(deleteUrl + idValue, deleteUrlMethod, "");
                    try {
                        System.out.println("데이터 삭제 요청에 대한 응답 코드: " + con.getResponseCode() + " -> "
                                + result.get("properties").get("Method").get("select").get("name").asText()
                                + "\t" + result.get("properties").get("Entry Point").get("title").get(0).get("text").get("content").asText());
                    } catch (Exception ignored) {
                        System.out.println("error");
                    }
                }
            }
        }
    }

    public JsonNode getDatabaseDate() throws IOException {
        ObjectNode json = objectMapper.createObjectNode();

        ObjectNode filterNode = objectMapper.createObjectNode();
        filterNode.put("property", "Entry Point");

        ObjectNode richTextNode = objectMapper.createObjectNode();
        richTextNode.put("contains", "");
        filterNode.put("rich_text", richTextNode);
        json.put("filter", filterNode);

        HttpURLConnection con = request.sendRequest(getUrl, getUrlMethod, objectMapper.writeValueAsString(json));

        // 응답 본문 읽기
        InputStream inputStream = con.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder responseStringBuilder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            responseStringBuilder.append(line);
        }

        // 응답 본문을 JSON으로 파싱
        String responseBody = responseStringBuilder.toString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        return jsonNode;
    }

}
