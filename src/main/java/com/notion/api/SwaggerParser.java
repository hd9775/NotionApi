package com.notion.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notion.api.database.CreateDatabaseData;


public class SwaggerParser {

    public static final String DATABASE_ID = "";
    public static final String AUTHORIZATION_Token = "";

    private static final CreateDatabaseData createDatabase = new CreateDatabaseData();

    public static void main(String[] args) {

        String json = "";

        try {
            String apiUrl = "http://localhost:8080/v2/api-docs";
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                json = response.toString();

                System.out.println("JSON 데이터를 가져왔습니다:");
                System.out.println("========================================");

                ObjectMapper objectMapper = new ObjectMapper();
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
                                createDatabase.method("GET", entry, entryNode.path(method), definitionMap);
                                break;
                            case "post":
                                createDatabase.method("POST", entry, entryNode.path(method), definitionMap);
                                break;
                            case "put":
                                createDatabase.method("PUT", entry, entryNode.path(method), definitionMap);
                                break;
                            case "delete":
                                createDatabase.method("DELETE", entry, entryNode.path(method), definitionMap);
                                break;
                            case "patch":
                                createDatabase.method("PATCH", entry, entryNode.path(method), definitionMap);
                                break;
                            default:
                                System.out.println("알 수 없는 메소드입니다.");
                                break;
                        }
                    }

                }
            } else {
                System.out.println("HTTP 요청 실패. 응답 코드: " + responseCode);
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
