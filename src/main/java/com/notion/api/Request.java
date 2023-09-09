package com.notion.api;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Request {
    public HttpURLConnection sendRequest(String url, String method, String jsonBody) throws IOException {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // HTTP 요청 메소드 설정
        con.setRequestMethod(method);

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

        return con;
    }
}
