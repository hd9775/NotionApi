package com.notion.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.notion.api.database.CreateDatabaseData;
import com.notion.api.database.DeleteDatabase;


public class SwaggerParser {

    public static final String DATABASE_ID = "";
    public static final String AUTHORIZATION_Token = "";

    private static final DeleteDatabase deleteDatabase = new DeleteDatabase();
    private static final CreateDatabaseData createDatabase = new CreateDatabaseData();

    public static void main(String[] args) {

        String json = "";

        try {
            String apiUrl = "http://localhost:8080/v3/api-docs";
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

                System.out.println("=========================");
                System.out.println("JSON 데이터를 가져왔습니다:");
                System.out.println("=========================");

                deleteDatabase.delete();

                System.out.println("=========================");
                System.out.println("데이터베이스 삭제 완료.");
                System.out.println("=========================");

                createDatabase.create(json);

                System.out.println("=========================");
                System.out.println("데이터베이스를 추가완료.");
                System.out.println("=========================");
            } else {
                System.out.println("HTTP 요청 실패. 응답 코드: " + responseCode);
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
