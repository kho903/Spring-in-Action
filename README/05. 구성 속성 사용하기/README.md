# 05. 구성 속성 사용하기
- 자동-구성(autoconfiguration)은 스프링 애플리케이션 개발을 굉장히 단순화 해줌. 그러나 스프링 XML 구성으로 속성 값을 설정하던 10년간 명시적으로 빈을 구성하지 않고 속성을 설정하는 마땅한 방법이 없었다.
- 다행히 스프링 부트는 구성 속성(configuration property)를 사용하는 방법 제공. 스프링 애플리케이션 컨텍스트에서 구성 속성은 빈의 속성. 그리고 JVM 시스템 속성, 명령행 인자, 환경 변수 등의 여러 가지
원천 속성 중에서 설정 가능.
- 이 장에서는 타코 클라우드 애플리케이션의 새로운 기능을 구현하는 것을 잠시 멈추고 구성 속성을 살펴본다.
- 구성 속성을 알아 두면 도움이 된다. 우선, 스프링 부트가 자동으로 구성하는 것을 세부 조정하기 위해 구성 속성을 사용하는 방법을 알아보자.
