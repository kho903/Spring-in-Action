# 3.1.4 타코와 주문 데이터 추가하기
- 지금까지 JdbcTemplate을 사용해 데이터베이스에 데이터를 저장하였다.
- JdbcIngredientRepository의 save() 메서드에는 JdbcTemplate의 update()를 사용해 Ingredient 객체를 데이터로 저장.
- 데이터를 저장할 때는 JdbcIngredientRepository에서 했던 것보다 더 많은 처리가 필요할 수 있다.
- JdbcTemplate의 update()를 사용해 데이터를 저장하는 방법은 다음 두 가지이다.
    - 직접 update()를 사용
    - SimpleJdbcInsert 래퍼 (Wrapper) 클래스 사용
- Ingredient 객체를 저장할 때보다 퍼시스턴스 처리가 복잡할 때는 어떻게 update()를 사용할까?
