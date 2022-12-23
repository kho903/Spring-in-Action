# 2.2 폼 제출 처리하기
- 뷰의 `<form>` 태그를 보면 POST 로 method 속성이 설정되어 있는데도 `<form>`에는 action 속성이 선언되지 않았다. 이 경우 폼이 제출되면 브라우저가 폼의 모든 데이터를 모아서 폼에 나타난
GET 요청과 같은 경로(/design)로 서버에 HTTP POST 요청 전송. 따라서 요청 처리 메서드 필요. /design 경로의 POST 요청을 처리하는 DesignTacoController 의 새로운 메서드를 작성하자.
- POST 요청을 위해 @PostMapping 어노테이션을 작성하자.
```java
// ...
public class DesignTacoController {
    // ... 
    @PostMapping
    public String processDesign(Taco design) {
        // TODO: 이 지점에서 타코 디자인(선택된 식자재 내역)을 저장.
        log.info("Processing design: " + design);
    
        return "redirect:/orders/current";
    }
}
```
- 클래스 수준의 @RequestMapping과 연관하여 processDesign() 메서드에 지정한 @PostMapping 애노테이션은 processDesign()이 /design 경로의 POSAT 요청을 처리함을 나타냄.
따라서 사용자의 타코 디자인 제출을 여기서 처리해야 한다.
- 타코 디자인 폼이 제출될 때 이 폼의 필드는 processDesign() 인자로 전달되는 Taco 객체의 속성과 바인딩된다. 따라서 processDesign() 메서드에서는 Taco 객체를 사용해 어떤 것이든
원하는 처리 가능.
- checkbox 요소들 여러 개는 모두 ingredients 이름을 가지며 텍스트 입력 요소의 이름은 name인 것을 알 수 있다. 이 필드들은 Taco 클래스의 ingredients 및 name 속성 값과 바인딩 됨.
- 폼의 Name 필드는 간단한 텍스트 값을 가질때만 필요. Taco 의 name 속성은 String 타입, 식자재 checkbox도 텍스트 값, but, checkbox는 복수개 가능 하므로 List<String> 타입.
- 지금은 processDesign() 메서드에 Taco 객체 관련 처리를 아무 것도 하지 않는다. 후에 TODO로 남겨두자.
- showDesignForm() 메서드처럼 processDesign()도 String 값을 반환하고 종료하며, 이 값도 사용자에게 보여주는 뷰를 나타냄. 그러나 차이점은 리디렉션 뷰를 나타내는 "redirect:"가
붙는다. 실행 후 /orders/current 상대 경로로 재접속되어야 한다는 것을 나타냄.
- 이로써 타코 생성 사용자는 주문 처리 폼으로 접속할 수 있다. 그러나 아직 컨트롤러가 없으니 만들어 보자.
- OrderController
```java
package tacos.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;
import tacos.Order;

@Slf4j
@Controller
@RequestMapping("/orders")
public class OrderController {

	@GetMapping("/current")
	public String orderForm(Model model) {
		model.addAttribute("order", new Order());
		return "orderForm";
	}

	@PostMapping
	public String processOrder(Order order) {
		log.info("Order submitted: " + order);
		return "redirect:/";
	}
}
```
- Order 클래스도 함께 만들자.
```java
package tacos;

import lombok.Data;

@Data
public class Order {

	private String deliveryName;
	private String deliveryStreet;
	private String deliveryCity;
	private String deliveryState;
	private String deliveryZip;
	private String ccNumber;
	private String ccExpiration;
	private String ccCVV;
}
```
- 먼저 OrderController에서 /orders로 시작되는 경로의 요청을 이 컨트롤러 요청 처리 메서드가 처리. 그리고 /orders/current 경로의 HTTP GET 요청을 orderForm() 메서드가 처리.
- 현재 orderForm() 메서드는 orderForm이라는 이름의 뷰를 반환만 함. 이후 변경 예정.
- orderForm 뷰는 다음과 같다.
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

<form method="POST" th:action="@{/orders}" th:object="${order}">
    <h1>Order your taco creations!</h1>

    <img th:src="@{/images/TacoCloud.png}"/> <a th:href="@{/design}"
                                                id="another">Design another taco</a><br/>

    <div th:if="${#fields.hasErrors()}">
			<span class="validationError"> Please correct the problems
				below and resubmit. </span>
    </div>

    <h3>Deliver my taco masterpieces to...</h3>

    <label for=" deliveryName">Name: </label>
    <input type="text" th:field="*{deliveryName}"/>
    <span class="validationError"
          th:if="${#fields.hasErrors('deliveryName')}"
          th:errors="*{deliveryName}">Name Error</span>
    <br/>

    <label for="deliveryStreet">Street address: </label>
    <input type="text" th:field="*{deliveryStreet}"/>
    <span class="validationError"
          th:if="${#fields.hasErrors('deliveryStreet')}"
          th:errors="*{deliveryStreet}">Street Error</span>
    <br/>

    <label for="deliveryCity">City: </label>
    <input type="text" th:field="*{deliveryCity}"/>
    <span class="validationError"
          th:if="${#fields.hasErrors('deliveryCity')}"
          th:errors="*{deliveryCity}">City Error</span>
    <br/>

    <label for="deliveryState">State: </label>
    <input type="text" th:field="*{deliveryState}"/>
    <span class="validationError"
          th:if="${#fields.hasErrors('deliveryState')}"
          th:errors="*{deliveryState}">State Error</span>
    <br/>

    <label for="deliveryZip">Zip code: </label>
    <input type="text" th:field="*{deliveryZip}"/>
    <span class="validationError"
          th:if="${#fields.hasErrors('deliveryZip')}"
          th:errors="*{deliveryZip}">Zip Error</span>
    <br/>

    <h3>Here's how I'll pay...</h3>
    <label for="ccNumber">Credit Card #: </label>
    <input type="text" th:field="*{ccNumber}"/>
    <span class="validationError"
          th:if="${#fields.hasErrors('ccNumber')}"
          th:errors="*{ccNumber}">CC Num Error</span>
    <br/>

    <label for="ccExpiration">Expiration: </label>
    <input type="text" th:field="*{ccExpiration}"/>
    <span class="validationError"
          th:if="${#fields.hasErrors('ccExpiration')}"
          th:errors="*{ccExpiration}">CC Num Error</span>
    <br/>

    <label for="ccCVV">CVV: </label>
    <input type="text" th:field="*{ccCVV}"/>
    <span class="validationError"
          th:if="${#fields.hasErrors('ccCVV')}"
          th:errors="*{ccCVV}">CC Num Error</span>
    <br/>

    <input type="submit" value="Submit order"/>
</form>
</body>
</html>
```
- 여기서 살펴볼 것은 form 태그에 action도 지정하고 있다. 액션이 지정되지 않을 경우에는 폼에 나타났던 것과 같은 URL로 폼의 HTTP POST 요청이 제출될 것이다. 그러나 여기서는 /orders
경로로 제출되도록 지정하고 있다.
- /orders 경로의 POST 요청을 처리하는 또 다른 메서드를 OrderController 클래스에 추가해야 한다. 현재는 간단하게 log를 찍자.
```java
package tacos.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;
import tacos.Order;

@Slf4j
@Controller
@RequestMapping("/orders")
public class OrderController {

	@GetMapping("/current")
	public String orderForm(Model model) {
		model.addAttribute("order", new Order());
		return "orderForm";
	}

	@PostMapping
	public String processOrder(Order order) {
		log.info("Order submitted: " + order);
		return "redirect:/";
	}
}
```
- 제출된 주문 처리위해 processOrder() 메서드가 호출될 때는 제출된 폼 필드와 바인딩 속성을 갖는 Order 객체가 인자로 전달된다. Taco 처럼 Order 는 주문 정보를 갖는 간단한 클래스다.
- Submit order 버튼을 누르면 주문 처리가 끝나고 타코 홈페이지가 나타날 것이다. 그리고 주문 정보를 보기 위해 찍어 놓았던 로그가 찍힐 것이다. 
```text
2022-12-23 15:15:10.872  INFO 1890 --- [nio-8080-exec-2] tacos.web.OrderController                : Order submitted: Order(deliveryName=Kim Ji Hun, deliveryStreet=adfasdfasfasdfasdf, deliveryCity=Suwon-si, deliveryState=Gyeonggi-do, deliveryZip=123, ccNumber=ㄴㅁㄹㅇ, ccExpiration=ㅇㄴㄹ, ccCVV=ㅁㅇㄴㄹ)
```
- 잘못된 정보 입력을 아직 허용한다. 따라서 우리가 필요한 정보에 맞도록 데이터를 검사해야 한다.

