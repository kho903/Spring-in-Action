# 4.4 사용자 인지하기
- 사용자가 로그인되었음을 아는 것 + 사용자 경험에 맞추려면 그들이 누군지 아는 것도 중요.
- 예를 들어, OrderController에서 주문 폼과 바인딩 되는 Order 객체를 최초 생성할 때 해당 주문하는 사용자 이름, 주소를 미리 넣을 수 있다면 좋은 것이다. 그러면 매번 주문때마다 다시 입력
필요가 없어진다. 또한 이보다 더 중요한 것으로 사용자 주문 데이터를 데이터베이스에 저장할 떄 주문이 생성되는 User와 Order를 연관시킬 수 있어야 한다.
- 데이터베이스에서 Order 개체(Entity)와 User 개체를 연관시키기 위해 아래와 같이 Order 에 새로운 속성을 추가하자..
```java
package tacos;
// ...

@Data
@Entity
@Table(name = "Taco_Order")
public class Order implements Serializable {
	//...

	@ManyToOne
	private User user;
	// ....
}
```
- user 속성의 @ManyToOne 애노테이션은 한 건의 주문이 한 명의 사용자에 속한다는 것을 나타내고, 한 명의 사용자는 여러 주문을 가질 수 있다.
- 주문을 처리하는 OrderController에서는 processOrder()가 주문을 저장하는 일 수행. 따라서 인증된 사용자가 누구인지 결정 후, Order 객체의 setUser() 호출해 해당 주문을 사용자와 
연결하도록 processOrder()를 수정해야 한다.
- 사용자가 누구인지 결정하는 방법은 여러 가지가 있으며, 그중 가장 많이 사용되는 방법은 다음과 같다.
  - Principal 객체를 컨트롤러 메서드에 주입
  - Authentication 객체를 컨트롤러 메서드에 주입
  - SecurityContextHolder를 사용해 보안 컨텍스트를 얻는다.
  - @AuthenticationPrincipal 애노테이션을 메서드에 지정
- 예를 들어 processOrder()에서 java.security.Principal 객체를 인자로 받도록 수정 가능. 그다음에 이 객체의 name 속성으로 UserRepository의 사용자를 찾을 수 있다.
```java
@PostMapping
public String procesOrder(@Valid Order order, Errors errors,
        SessionStatus sessionStatus,
        Principal principal) {

    ... 
    User user = userRepository.findByUsername(principal.getName());
	order.setUser(user);
    ...
}
```
- 이 코드는 잘 동작하지만, 보안과 관련 없는 코드가 혼재. Principal 대신 Authentication 객체를 인자로 받도록 변경 가능
```java
@PostMapping
public String processOrder(
        @Valid Order order,
        Errors errors,
        SessionStatus sessionStatus,
        Authentication authentication) {
    ...
    User user = (User) authentication.getPrincipal();
	
	order.setUser(user);
    ...
}
```
- 이 코드에서는 Authentication 객체를 얻은 후 getPrincipal() 호출해 Principal 객체(여기서는 User)를 얻는다. 단, getPrincipal()은 java.util.Object 타입을 반환하므로
User 타입으로 변환해야 한다.
- 그러나 다음과 같이 processOrder()의 인자로 User 객체를 전달하는 것이 가장 명쾌한 해결 방법. 단, User 객체에 @Authentication Principal 애노테이션을 짖어해야 한다.
```java
package tacos.web;

//...

public class OrderController {

	// ...

	@PostMapping
	public String processOrder(@Valid Order order, Errors errors,
			SessionStatus sessionStatus, @AuthenticationPrincipal User user) {
		if (errors.hasErrors()) {
			return "orderForm";
		}

		order.setUser(user);

		orderRepository.save(order);
		sessionStatus.setComplete();
		return "redirect:/";
	}
}
```
- @AuthenticationPrincipal 의 장점은 타입 변환 필요 없고 Authentication과 동일하게 보안 특정 코드만 갖는다. 일단 User 객체가 processOrder()에 전달되면 해당 주문(Order 객체)
에서 사용할 준비가 된 것이다.
- 보안 큭정 코드가 많아 조금 어려워 보이지만 인증된 사용자가 누구인지 식별하는 방법이 하나 더 있다. 즉, 보안 컨텍스트로부터 Authentication 객체를 얻은 후 다음과 같이 Principal 객체
(인증된 사용자)를 요청하면 된다. 이떄도 반환되는 객체를 User 타입으로 변환해야 한다.
```text
Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
User user = (User) authentication.getPrincipal();
```
- 이 코드에는 보안 특정 코드가 많다. 그러나 다른 방법에 비해 한 가지 장점은 컨트롤러의 처리 메서드는 물론, 애플리케이션의 어디서든 사용 가능하다는 점이다.
- 사요앚와 주문을 연관시키는 것에 추가해 현재 주문을 하는 인증된 사용의 이름과 주소를 주문 폼에 미리 채워 보여주면 좋다. 그러면 사용자가 매번 주문을 할 때마다 이름과 주소를 다시 입력할
필요가 없기 떄문. OrderController의 orderForm()을 변경하자.
```java
@GetMapping("/current")
public String orderForm(@AuthenticationPrincipal User user,
        @ModelAttribute Order order) {

    if (order.getDeliveryName() == null) {
        order.setDeliveryName(user.getFullName());
    }
    if (order.getDeliveryStreet() == null) {
        order.setDeliveryStreet(user.getStreet());
    }
    if (order.getDeliveryCity() == null) {
        order.setDeliveryCity(user.getCity());
    }
    if (order.getDeliveryState() == null) {
        order.setDeliveryState(user.getState());
    }
    if (order.getDeliveryZip() == null) {
        order.setDeliveryZip(user.getZip());
    }

    return "orderForm";
}
```
- 여기서는 인증된 사용자(User 객체)를 메서드 인자로 받아 해당 사용자의 이름과 주소를 Order 객체의 각 속성에 설정한다. 이렇게 하면 주문의 GET 요청이 제출될 때 해당 사용자의 이름과 주소가
미리 채워진 상태로 주문 폼이 전송될 수 있다.
- 주문 외에도 인증된 사용자 정보를 활용할 곳이 하나 더 있다. 즉, 사용자가 원하는 식자재를 선택해 타코를 생성하는 디자인 폼에서 현재 사용자의 이름을 보여줄 것이다. 이때 UserRepository의 
findByUsername()을 사용해 현재 디자인 폼으로 작업 중인 인증된 사용자를 찾아야 한다. DesignTacoController에서 다음과 같이 변경하자.

```java
package tacos.web;

// ...

public class DesignTacoController {

	private final IngredientRepository ingredientRepository;

	private TacoRepository tacoRepository;

	private UserRepository userRepository;

	public DesignTacoController(IngredientRepository ingredientRepository, TacoRepository tacoRepository,
		UserRepository userRepository) {
		this.ingredientRepository = ingredientRepository;
		this.tacoRepository = tacoRepository;
		this.userRepository = userRepository;
	}

	// ...
    
	@PostMapping
	public String processDesign(@Valid Taco design, Errors errors,
			@ModelAttribute Order order) {
		if (errors.hasErrors()) {
			return "design";
		}

		Taco saved = tacoRepository.save(design);
		order.addDesign(saved);

		return "redirect:/orders/current";
	}
}
```
