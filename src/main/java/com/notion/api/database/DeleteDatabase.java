package com.notion.api.database;

import com.notion.api.SwaggerParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DeleteDatabase {
    String url = "https://api.notion.com/v1/pages";

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
            wr.write(jsonBody.getBytes("UTF-8"));
            wr.flush();
            wr.close();

            // 응답 코드 확인
            int responseCode = con.getResponseCode();
            System.out.println(entry + " " + methodType + "에 대한 " + "POST" + " 요청에 대한 응답 코드: " + responseCode);

            // 응답 내용 읽기
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // 응답 내용 출력
            System.out.println("---응답 내용:\n" + response.toString());
        } catch (Exception e) {
        }
    }

}
