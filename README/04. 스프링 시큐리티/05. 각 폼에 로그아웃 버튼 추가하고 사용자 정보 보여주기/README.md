# 4.5 각 폼에 로그아웃 버튼 추가하고 사용자 정보 보여주기
- 마지막으로, 로그아웃 버튼, 사용자 정보 보여주는 필드 추가.
- 우선, 사용자가타코를 생성할 수 있는 디자인 페이지로 이동하는 참조와 로그아웃 버튼을 홈페이지에 추가.
- home.html
```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Taco Cloud</title>
</head>
<body>
    <h1>Welcome to...</h1>
    <img th:src="@{/images/TacoCloud.png}"/>

    <form method="POST" th:action="@{/logout}" id="logoutForm">
        <input type="submit" value="Logout" />
    </form>

    <a th:href="@{/design}" id="design">Design a taco</a>
</body>
</html>
```
- 다음은 design.html에 로그아웃 버튼 추가.
```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Taco Cloud</title>
    <link rel="stylesheet" th:href="@{/styles.css}"/>
</head>
<body>
<h1>Design your taco!</h1>
<h2>Feelin' hungry, <span th:text="${user.fullName}">NAME</span>?</h2>
<img th:src="@{/images/TacoCloud.png}"/>

<form method="POST" th:action="@{/logout}" id="logoutForm">
    <input type="submit" value="Logout" />
</form>

<form method="POST" th:object="${taco}" th:action="@{/design}" id="tacoForm">
    // ... 
</form>
</body>
</html>
```
- 다음은 사용자가 생성한 타코를 주문할 수 ㅇ씨는 주문 페이지에 로그아웃 버튼 추가.
- orderForm.html
```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="EUC-KR">
    <title>Taco Cloud</title>
    <link rel="stylesheet" th:href="@{/styles.css}"/>
</head>
<body>

<form method="POST" th:action="@{/logout}" id="logoutForm">
    <input type="submit" value="Logout" />
</form>

<form method="POST" th:action="@{/orders}" th:object="${order}" id="orderForm">
    <h1>Order your taco creations!</h1>

    <img th:src="@{/images/TacoCloud.png}"/> <a th:href="@{/design}"
                                                id="another">Design another taco</a><br/>

    <h3>Your tacos in this order:</h3>

    // ...
</form>
</body>
</html>
```
- 이제는 타코 클라우드 애플리케이션에서 필요한 스프링 시큐리티 구성, 커스텀 사용자 스토어 구성, 로그인 페이지 작성, 인증된 사용자 정보 파악이 모두 완료되었다. 확인해보자.
