# 2.5 뷰 템플릿 라이브러리 선택하기
- 개인 취향에 따라 뷰 템플릿 라이브러리를 선택. 스프링은 유연성이 좋아 다양한 템플릿 지원. 극히 소수(Thymeleaf의 스프링 시큐리티 dialect)를 제외하고, 우리가 선택하는 템플릿 라이브러리는
자신이 스프링과 함께 동작하는 것 조차도 모른다. 그정도로 스프링의 유연성이 좋다.
- 스프링에서 지원되는 템플릿

| 템플릿                   | 스프링 부트 스타터 의존성                       |
|-----------------------|--------------------------------------|
| FreeMarker            | spring-boot-starter-freemarker       |
| Groovy 템플릿            | spring-boot-starter-groovy-templates |
| JavaServer Pages(JSP) | 없음(Tomcat이나 Jetty 서블릿 컨테이너 자체에서 제공됨) |
| Mustache              | spring-boot-starter-mustache         |
| Thymeleaf             | spring-boot-starter-thymeleaf        |

- 대개의 경우 원하는 뷰 템플릿 선택하고 의존성 추가 후 /templates 디렉터리에 템플릿을 작성. 그러면 스프링 부트는 템플릿 라이브러리를 찾아서 스프링 MVC 컨트롤러의 뷰로 사용할 컴포넌트를
자동으로 구성.
- 지금까지 Thymeleaf를 사용하였다. Thymeleaf 스타터는 pom.xml에 추가되어 있고, 애플리케이션이 시작될 때마다 스프링 부트 자동-구성에서 Thymeleaf를 찾은 후 자동으로 Thymeleaf 빈을
구성해 준다. 따라서 우리는 /templates 디렉터리에 템플릿만 작성하면 된다.
- 다른 템플릿 라이브러리 사용하고 싶으면 기존의 프로젝트 빌드 명세를 수정하면 도니다.
- 표를 보면 JSP는 의존성을 지정하지 않는다. 서블릿 컨테이너(기본적으로 톰캣) 자신이 JSP 명세를 구현하므로 스프링 부트의 스타터로 지정할 필요가 없기 때문이다.
- 그러나 JSP 선택시 추가로 고려해야 할 것이 있다. 내장된 톰캣과 제티 컨테이너를 포함해 자바 서블릿 컨테이너는 /WEB-INF 밑에서 JSP 코드를 찾는다. 그러나 우리 애플리케이션을 실행 가능한
JAR 파일로 생성한다면 요구사항 충족 불가능. 따라서 WAR 파일로 생성하고 종전의 서블릿 컨테이너에 설치하는 경우에는 JSP를 선택해야 한다.

## 2.5.1 템플릿 캐싱
- 기본적으로 템플릿은 최초 사용될 때 한 번만 파싱(코드 분석)된다. 그리고 파싱된 결과는 향후 사용을 위해 캐시에 저장됨. 이것은 프로덕션에서 애플리케이션을 실행할 때 좋은 기능이다. 매번 요청
처리시 불필요하게 템플릿 파싱을 하지 않으므로 성능을 향상시킬 수 있다.
- 그러나 개발 시에는 수정 후 새로고침을 해도 수정 전의 페이지를 보게 될 것이다. 이 경우 애플리케이션을 다시 시작하는 방법밖에 없다. 이러한 템플릿 캐싱ㅇ르 비활성화 하는 방법이 있다. 각 템플릿의
캐싱 속성만 false로 설정하면 된다.

| 템플릿              | 캐싱 속성                        |
|------------------|------------------------------|
| FreeMarker       | spring.freemarker.cache      |
| Groovy Templates | spring.groovy.template.cache |
| Mustache         | spring.mustache.cache        |
| Thymeleaf        | spring.thymeleaf.cache       |

- 기본적으로 모든 캐싱 활성화는 true로 지정되어 있어, 비활성화 할 때에는 false로 설정해야 한다. application.properties에서 추가하면 된다.
```text
spring.thymeleaf.cache=false
```
- 단, 프로덕션에서 애플리케이션을 배포할 떄는 방금 추가한 설정을 삭제 또는 true로 변경해야 한다. 이외에도 프로파일에 해당 속성을 설정하는 방법이 있다.
- 하지만 스프링 부트의 DevTools를 사용하는 것이 훨씬 쉽다. 많은 도움을 제공하며, 모든 템플릿 라이브러리의 캐싱을 비활성화 하지만 배포시에는 DevTools 자신이 비활성화됨.

