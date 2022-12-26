# 2.3 폼 입력 유효성 검사하기
- 각 필드들의 유효성 검사를 아직 하지 않았다. 
- 폼의 유효성 검사를 하는 한 가지 방법으로 processDesign() 과 processOrder() 메서드에 수많은 if/then 블록을 너저분하게 추가하는 것이 있다. 이 경우 데이터가 적합한지 각 필드에서 일일이
확인해야 한다. 그것은 디버깅이 어렵다.
- 스프링은 자바의 빈 유효성 검사(Bean Validation) API(JSR-303)를 지원. 애플리케이션에 추가 코드 작성 없이 유효성 검사 규칙 쉽게 선언 가능. 스프링 부트로 유효성 검사 라이브러리 
쉽게 추가 가능. 유효성 검사 API와 이 API를 구현한 Hibernate(하이버네이트) 컴포넌트는 스프링 부트의 웹 스타터 의존성으로 자동 추가되기 때문.
- 스프링 MVC에 유효성 검사를 적요하려면 다음과 같이 해야 한다.
  - 유효성을 검사할 클래스(여기서 Taco, Order)에 검사 규칙을 선언.
  - 유효성 검사를 해야 하는 컨트롤러 메서드에 검사를 수행한다는 것을 지정. 여기서는 DesignTacoController의 processDesign(), OrderController의 processOrder()
  - 검사 에러를 보여주도록 폼 뷰를 수정.
- 유효성 검사 API는 몇 가지 애노테이션을 제공. 이 애노테이션들은 검사 규칙을 선언하기 위해 도메인 객체의 속성에 지정 가능.

## 2.3.1 유효성 검사 규칙 선언하기
- Taco 클래스에서 name 속성의 값이 없거나 null인지 확인, 최소한 하나 이상의 식자재 항목을 선택했는지 확인 필요. @NotNull, @Size 사용
```java
package tacos;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class Taco {

	@NotNull
	@Size(min = 5, message = "Name must be at least 5 characters long")
	private String name;

	@Size(min = 1, message = "You must choose at least 1 ingredient")
	private List<String> ingredients;
}
```
- Order 클래스에서는 속성들에 사용자가 입력을 하지 않은 필드가 있는지 확인만 하면 되므로, @NotBlanck 애노테이션을 사용.
- 대금지불의 경우 ccNumber 입력 값이 유효한 신용카드 번호인지도 확인 필요. ccExpiration은 MM/YY 형식의 값이 어야 하고, ccCVV는 세 자리 수가 되어야 한다.
- 이런 종류의 유효성 검사를 하려면 자바 빈 유효성 검사 API 애노테이션과 Hibernate Validator의 또 다른 애노테이션을 사용해야 한다.
```java
package tacos;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.CreditCardNumber;

import lombok.Data;

@Data
public class Order {

	@NotBlank(message = "Name is required")
	private String deliveryName;

	@NotBlank(message = "Street is required")
	private String deliveryStreet;

	@NotBlank(message = "City is required")
	private String deliveryCity;

	@NotBlank(message = "State is required")
	private String deliveryState;

	@NotBlank(message = "Zip is required")
	private String deliveryZip;

	@CreditCardNumber(message = "Not a valid credit card Number")
	private String ccNumber;

	@Pattern(regexp="^(0[1-9]|1[0-2])([\\/])([1-9][0-9])$",
		message="Must be formatted MM/YY")
	private String ccExpiration;

	@Digits(integer = 3, fraction = 0, message = "Invalid CVV")
	private String ccCVV;
}
```
- ccNumber 속성에는 @CreditCardNumber가 지정되어 있다. 이 애노테이션은 속성의 값이 Luhn(룬) 알고리즘 검사에 합격한 유효한 신용카드 번호이어야 한다는 것을 선언. 이 알고리즘 검사는
사용자의 입력 실수, 고의적인 악성 데이터 방지, but, 실제 존재하는 신용카드 번호인지 검사는 못함.(금융망 연동 필요)
- ccExpiration 속성은 @Pattern 애노테이션으로 정규 표현식을 지정하여 해당 형긱을 따르는지 확인.
- ccCVV 속성은 @Digits 애노테이션으로 입력 값이 정확하게 세 자리 숫자인지 검사.
- 모든 유효성 겁사 애노테이션은 message 속성을 가지고 있다. 사용자가 입력한 정보가 유효성 규칙을 위반할 때 보여줄 메시지를 정의.

## 2.3.2 폼과 바인딩 될 때 유효성 검사 수행하기
- 각 폼의 POST 요청이 관련 메서드에서 처리될 떄 유효성 검사가 수행되도록 컨트롤러 수정 필요
- Taco 의 유효성 검사를 하려면 DesignTacoController의 processDesign() 메서드 인자로 전달되는 Taco에 자바 빈 유효성 검사 API의 @Valid 애노테이션을 추가해야 한다.
```java
package tacos.web;

// ...

@Slf4j
@Controller
@RequestMapping("/design")
public class DesignTacoController {

      // ... 
      @PostMapping
      public String processDesign(@Valid Taco design, Errors errors) {
          if (errors.hasErrors()) {
              return "design";
          }
          // TODO: 이 지점에서 타코 디자인(선택된 식자재 내역)을 저장.
          log.info("Processing design: " + design);
          
          return "redirect:/orders/current";
      }
}
```
- @Valid 애노테이션은 제출된 Taco 객체의 유효성 검사를 수행(제출된 폼 데이터와 Taco 객체가 바인딩된 후, 그리고 processDesign() 코드가 실행되기 전에)하라고 스프링 MVC에 알려줌.
만일 에러 발생시, Errors 객체에 저장되어 processDesign()으로 전달됨. 
- Errors 객체에 hasErrors() 메서드로 에러가 있는 지 확인 가능. 그리고 에러가 있으면 Taco의 처리를 중지하고 "design" 뷰 이름 반환하여 폼이 다시 보이게 한다.
- Order의 유효성 검사도 유사하게 가능.
```java
package tacos.web;

// ...

@Slf4j
@Controller
@RequestMapping("/orders")
public class OrderController {

    // ... 
    @PostMapping
    public String processOrder(@Valid Order order, Errors errors) {
        if (errors.hasErrors()) {
            return "orderForm";
        }
        log.info("Order submitted: " + order);
        return "redirect:/";
    }
}
```

## 2.3.3 유효성 검사 에러 보여주기
```html
<label for="ccCVV">CVV: </label>
<input type="text" th:field="*{ccCVV}"/>
<span class="validationError"
      th:if="${#fields.hasErrors('ccCVV')}"
      th:errors="*{ccCVV}">CC Num Error</span>
```
- `<span>`요소의 class 속성은 사용자의 주의를 끌기 위한 에러의 명칭을 지정하는 데 사용. th:if 속성에서는 이 `<span>`을 보여줄지 말지를 결정하며, 이때 fields 속성의 hasErrors()를
사용해 ccNumber 필드에 에러가 있는지 검사. 있으면 `<span>`이 나타남
- th:errors 속성은 ccNumber 필드를 참조. 에러가 있으면 사전 지정된 메시지를 검사 에러 메시지로 교체한다.
- 여기서는 name, city, ZIP code 필드에 입력을 안 했다는 에러를 표시한다.
- 이제 타코 클라우드 컨트롤러가 데이터를 보여주고 입력을 받는 것은 물로, 기본적인 유효성 검사 규칙을 충족하는지 검사한다.

