# 3.1 JDBC를 사용해서 데이터 읽고 쓰기
- 관계형 데이터베이스와 SQL은 데이터 퍼시스턴스의 최우선 선택의 자리를 지켜왔다. 
- 관계형 데이터를 사용할 경우 자바 개발자들이 선택할 대표적인 두 가지 방법은 JDBC와 JPA. 스프링은 두 가지 모두 지원, 사용하지 않을 때에 비해 더 쉽게 JDBC, JPA를 사용할 수 있도록 해준다.
- 스프링의 JDBC 지원은 JdbcTemplate 클래스에 기반을 둔다. JdbcTemplate은 JDBC를 사용할 때 요구되는 모든 형식적이고 상투적인 코드없이 개발자가 관계형 데이터베이스에 대한 SQL 연산을 수행할
수 있는 방법을 제공.
- JdbcTemplate을 사용하지 않고 데이터베이스 쿼리하는 방법은 다음과 같다.
```java
@Override
public Ingredient findById(String id) {
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    try {
        connection = dataSource.getConnection();
        statement = connection.prepareStatement(
        "select id, name, type from Ingredient where id = ?");
        statement.setString(1, id);
        resultSet = statement.executeQuery();
        Ingredient ingredient = null;
        if (resultSet.next()) {
            ingredient = new Ingredient(
            resultSet.getString("id"),
            resultSet.getString("name"),
            Ingredient.Type.valueOf(resultSet.getString("type")));
        }
        return ingredient;
    } catch (SQLException e) {
    
    } finally {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) { }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) { }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) { }
        }
    }
    return null;
}
```
- 위 코드 어딘가에 식자재가 저장된 데이터베이스를 쿼리하는 코드가 있는데 JDBC 코드 내에서 쿼리를 찾기 힘들다. 데이터베이스 연결 (connection) 생성, 명령문(statement) 생성, 그리고 연결과 명령문 및
결과 세트 (result set)를 닫고 클린업하는 코드들로 쿼리 코드가 둘러싸여 있기 때문이다.
- 추가로 해당 쿼리를 수행할 때 잘못될만한 일이 많아 SQLException 처리를 해야 한다.
- SQLException은 catch 블록으로 반드시 처리해야 하는 checked 예외다. 그러나 데이터베이스 연결 생성 실패나 작성 오류가 있는 쿼리와 같은 대부분의 흔한 문제들은 catch 블록에서 해결될 수 없어 현재
메서드를 호출한 상위 코드로 예외 처리를 넘겨야 한다. 
- 다음에서 이것과 대조되는 JdbcTemplate 사용 메서드를 보자.
```text
private JdbcTemplate jdbc;

@Override
public Ingredient findById(String id) {
    return jdbc.queryForObject(
        "select id, name, type from Ingredient where id=?",
        this::mapRowToIngredient, id);
}

private Ingredient mapRowToIngredient(ResultSet rs, int rowNum) 
        throws SQLException {
        
    return new Ingredient(
        rs.getString("id"),
        rs.getString("name"),
        Ingredient.Type.valueOf(rs.getString("type")));
}
```
- JdbcTemplate를 사용하면 훨씬 간단해진다. 명령문이나 데이터베이스 연결 객체를 생성하는 코드가 아예 없다. 그리고 메서드의 실행이 끝난 후 그런 객체들을 클린업하는 코드 또한 없다. 또한, catch 블록에서
올바르게 처리할 수 없는 예외를 처리하는 어떤 코드도 없다. 쿼리를 수행하고 (JdbcTemplate의 queryForObject()), 그 결과를 Ingredient 객체로 생성하는 (mapRowToIngredient()) 것에 초점을 두는
코드만 존재.
- 다음으로 우리 애플리케이션에 JDBC 퍼시스턴스를 추가해보자.
