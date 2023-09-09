# NotionApi

swagger api-docs 에서 json파일을 받아 notion database에 api 명세서 생성

### SwaggerParser.java 파일에 아래 내용 작성

> authorizationToken <- Notion security code <br>
> [Notion Integrations](https://www.notion.so/my-integrations) 에서 api secret key 발급후
> 원하는 데이터 베이스 페이지에 api 권한 부여
> 
> ![image](https://github.com/hd9775/NotionApi/assets/12166357/08e9a6b5-b2fd-426c-91af-c826e3cd4f29)


> databaseId <- database id
>
> ![image](https://github.com/hd9775/NotionApi/assets/12166357/77bff7b1-1052-455d-b84a-6aaf49ffd3cf)

### JsonBuilder.java 파일 본인의 데이터베이스 구조에 맞게 수정

> 아래는 현재 작성된 데이터베이스 구조
> ![image](https://github.com/hd9775/NotionApi/assets/12166357/164605c8-a14d-4b74-9350-f89c528794d6)
