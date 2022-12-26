# 2.4 뷰 컨트롤러로 작업하기
- 각 컨트롤러는 애플리케이션의 서로 다른 기능을 제공하지만 프로그래밍 패턴은 다음과 같이 동일하다.
  - 스프링 컴포넌트 검색에서 자동으로 찾은 후 스프링 애플리케이션 컨텍스트의 빈(bean)으로 생성되는 컨트롤러 클래스임을 나타내기 위해 그것들 모두 @Controller 애노테이션을 사용.
  - HomeController 외의 다른 컨트롤러에서는 자신이 처리하는 요청 패턴을 정의하기 위해 클래스 수준의 @RequestMapping 애노테이션 사용.
  - 메서드에서 어떤 종류의 요청을 처리해야 하는지 나타내기 위해 @GetMapping 또는 @PostMapping 애노테이션이 지정된 하나 이상의 메서드를 갖는다.
- 우리가 작성했던 대부분의 컨트롤러는 이 패턴을 따르는데, HomeController와 같이 모델 데이터나 사용자 입력을 처리하지 않는 간단한 컨트롤러의 경우는 다른 방법으로 정의 가능.
- 뷰에 요청을 전달하는 일만하는 컨트롤러(뷰 컨트롤러)는 다음과 같이 선언 가능.
```java
package tacos.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("home");
	}
}

```
- WebConfig는 뷰 컨트롤러의 역할을 수행하는 구성 클래스이며 가장 중요한 것은 WebMvcConfigurer 인터페이스를 구현한다는 것. WebMvcConfigurer는 스프링 MVC를 구성하는 메서드를 정의하고
있다. 그리고 인터페이스이지만 정의된 모든 메서드의 기본적인 구현을 제공. 따라서 우리가 필요한 메서드만 오버라이딩하면 된다.
- addViewController() 메서드는 하나 이상의 뷰 컨트롤러를 등록하기 위해 사용할 수 있는 ViewControllerRegisty를 인자로 받는다. 여기서는 우리의 뷰 컨트롤러가 GET 요청을 처리하는 
경로인 "/"를 인자로 전달하여 addViewController()를 호출. 이 메서드는 ViewControllerRegistration 객체를 반환. 그리고 "/" 경로의 요청이 전달되어야 하는 뷰로 home을 지정하기 위해
연달아 ViewControllerRegistration 객체의 setViewName()을 호출.
- 이렇게 함으로써 구성 클래스(WebConfig)의 몇 줄 안되는 코드로 HomeController를 대체할 수 있다. 이제는 HomeController를 삭제해도 잘 실행된다.
- 여기서는 뷰 컨트롤러 선언을 위해 WebConfig를 생성하였지만 어떤 구성 클래스에서도 WebMvcConfigurer 인터페이스를 구현하고 addViewController 메서드를 오버라이딩할 수 있다.
- 예를 들어 부트스트랩 클래스인 TacoCloudApplication에 추가 가능.
```java
@SpringBootApplication
public class TacoCloudApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(TacoCloudApplication.class, args);
    }
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
    }
}
```
- 기존 구성 클래스를 확장하면 새로운 구성 클래스의 생성을 피할 수 있어서 프로젝트 파일 개숟고 줄어든다는 장점이 있지만, 구성 클래스는 간단하게 유지하되, 서로 다른 종류의 구성 클래스를 새로
생성하는 편이 더 나을 수 있다.
