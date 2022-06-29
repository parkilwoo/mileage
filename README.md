# mileage
Mileage Rest API

> ### 실행방법

해당 프로젝트는 지바11로 만들어졌습니다. **jdk11 이상의 버전이 필수입니다.**

소스를 그대로 Clone을 하여 서버를 Run 하거나, jar파일로 구동을 해주시면 됩니다.(-Dserver port='원하는포트' -jar 'jar경로')

데이터베이스는 인메모리DB인 **H2 Database**를 사용하였습니다. (MODE를 Mysql모드로 하여 문법은 Mysql 문법입니다.)</br>
<span style="color:red">->따라서 서버를 재기동 할 시 Database가 초기화 되는점 유의 바랍니다.</span></br>
DDL은 프로젝트내 /resource/h2/schema.sql을 확인하시면 됩니다.

reviewId, userId, placeId, photoId 값들은 데이터베이스에 해당 값들이 없을경우 Insert를 먼저 진행하고 있습니다.<br/>
ex) 서버 구동 후</br>
{
"type": "REVIEW",
"action": "ADD",
"reviewId": "240a0658-dc5f-4878-9381-ebb7b2667772",
"content": " !",
"attachedPhotoIds": ["e4d1a64e-a531-46de-88d0-ff0ed70c0bb8", "afb0cef2-
851d-4a50-bb07-9cc15cbdc332"],
 "userId": "3ede0ef2-92b7-4817-a5f3-0c575361f745",
 "placeId": "2e4baf1c-5acb-4efb-a1af-eddada31b00f"
}</br>
위의 값으로 최초로 리뷰 등록시 reviewId, photoId, userId, placeId 가 각각의 테이블에 Insert 됩니다.

> ### API 정의 
<h4>포인트 적립 API : method: post & url: /event로 요청하시면 됩니다. (파라미터는 json형태로)</h4>
<h4>포인트 조회 API : method: get & url: /event/point/{userId}로 요청하시면 됩니다. userId에는 조회할 유저의 userId(UUID)를 넣어주시면 됩니다.</h4>
ex) /event/point/3ede0ef2-92b7-4817-a5f3-0c575361f745






