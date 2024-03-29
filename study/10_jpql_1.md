# 객체지향 쿼리 언어1

## JPA는 다양한 쿼리 방법을 지원

* **JPQL**
* JPA Criteria
* **QueryDSL**
* 네이티브 SQL
* JDBC API 직접 사용, MyBatis, SpringJdbcTemplate 함께 사용

## JPQL 소개

* 가장 단순한 조회 방법
  * EntityManager.find()
  * 객체 그래프 탐색(a.getB().getC())
  
* **나이가 18살 이상인 회원을 모두 검색하고 싶다면?**

## JPQL         

* JPA를 사용하면 엔티티 객체를 중심으로 개발
* 문제는 검색 쿼리
* 검색을 할 때도 **테이블이 아닌 엔티티 객체를 대상으로 검색**
* 모든 DB 데이터를 객체로 변환해서 검색하는 것은 불가능
* 애플리케이션이 필요한 데이터만 DB에서 불러오려면 결국 검색 조건이 포함된 SQL이 필요


* JPA는 SQL을 추상화한 JPQL이라는 객체 지향 쿼리 언어 제공
* SQL과 문법 유사, SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN 지원
* JPQL은 엔티티 객체를 대상으로 쿼리
* SQL은 데이터베이스 테이블을 대상으로 쿼리


``` java

  String qlString = "select m From Member m where m.username like '%kim%'";
            
  List<Member> result = em.createQuery(
           qlString,
           Member.class
  ).getResultList();

```

```sql
  Hibernate: 
    /* select
        m 
    From
        Member m 
    where
        m.username like '%kim%' */ select
            member0_.MEMBER_ID as member_i1_0_,
            member0_.USERNAME as username2_0_
        from
            Member member0_
        where
            member0_.USERNAME like '%kim%'
            
```

* 테이블이 아닌 객체를 대상으로 검색하는 객체 지향 쿼리
* SQl을 추상화해서 특정 데이터베이스 SQL에 의존X
* JPQL을 한마디로 정의하면 객체 지향 SQL

## Criteria 소개

* 문자가 아닌 자바코드로 JPQL를 작성할 수 있음
* JPQL 빌더 역할
* JPA 공식 기능
* 단점: 너무 복잡하고 실용성이 없다.
* Criteria 대신에 **QueryDSL 사용 권장**

``` java

  CriteriaBuilder cb = em.getCriteriaBuilder();
  CriteriaQuery<Member> query = cb.createQuery(Member.class);

  Root<Member> m = query.from(Member.class);

  CriteriaQuery<Member> cq = query.select(m).where(cb.equal(m.get("username"), "kim"));
  List<Member> resultList = em.createQuery(cq).getResultList();
  
``` 

## 이해하기
* 쉬운거 같으면서 어려움
* 컴파일 오류 시점에서 찾아줘서 장점이 있음
* 동적쿼리 짜기에는 좋음

## QueryDSL 소개

* 문자가 아닌 자바코드를 JPQL을 작성할 수 있음
* JPQL 빌더 역할
* 컴파일 시점에서 문법 오류를 찾을 수 있음
* 동적쿼리 작성 편리함
* 단순하고 쉬움
* 실무 사용 권장

``` java
  
  //JPQL
  //select m from Member m where m.age > 18
      
  JPAFactoryQuery query = new JPAQueryFactory(em);
  QMember m = QMember.member;
  
  List<Member> list = 
      query.selectFrom(m)
           .where(m.age.gt(18))
           .orderBy(m.name.desc())
           .fetch();
           
```

## 네이티브 SQL 소개

* JPA가 제공하는 SQL을 직접 사용하는 기능
* JPQL로 해결할 수 없는 특정 데이터베이스에 의존적인 기능
* 예) 오라클 CONNECT BY, 특정 DB만 사용하는 SQL 힌트


``` java

  String qlString = "select MEMBER_ID, USERNAME from MEMBER ";
  
  em.createNativeQuery(qlString, Member.class)
        .getResultList();
           
```

## JDBC 직접사용, SpringJdbcTemplate 등

* JPA를 사용하면서 JDBC 커넥션을 직접 사용하거나, 스프링 JdbcTemplate, 마이바티스등을 함꼐 사용할 수 있다.
* 단, 영속성 컨텍스트를 적절한 시점에 강제로 플러시 하는 것이 필요하다.
  * ***참고: executeQuery 하기전에 강제로 em.flush() 해줘야됨*** 
* 예) JPA를 우회해서 SQL을 실행하기 직전에 영속성 컨텍스트 수동 플러시


# JPQL 기본 문법


***ANSI SQL란***
***ANSI, American National Standards Institute(미국 표준 협회)가 각기 다른 DBMS(Oracle, MySQL 등)에서 공통적으로 사용할 수 있도록 고안한 표준 SQL문 작성방법입니다.***

## JPQL 소개

* Java Persistence Query Language

* JPQL은 객체지향 쿼리 언어다. 따라서 테이블을 대상으로 쿼리하는 것이 아니라 엔티티 객체를 대상으로 쿼리 한다.

* JPQL은 엔티티 객체를 대상으로 쿼리 를 질의하고
  * JPQL은 데이터베이스 테이블을 대상으로 쿼리 를 질의한다.
  * JPQL은 SQL을 추상화해서 특정 데이터베이스 SQL에 의존하지 않는다.

* JPQL은 결국 SQL로 변환된다.


![](https://github.com/dididiri1/jpabook/blob/main/images/10_01.png?raw=true)

### JPQL 문법

* select m form **Member** as m where **m.age** > 18
* 엔티티와 속성은 대소문자 구분O (Member, age)
* JPQL 키워드 대소문자 구분X (SELECT, FROM, where)
* 엔티티 이름 사용 테이블 이름이 아닌(Member)
* **별칭은 필수(m)** (as는 생략가능)

## 집합과 정렬

```sql
select_문 :: = 
    select_절
    from_절
    [where_절]
    [groupby_절]
    [having_절]
    [orderby_절]
```

```sql
update_문 :: = update_절 [where_절]
delete_문 :: = delete_절 [where_절]
```

## TypeQuery, Query

* TypeQuery: 반환 타입이 명확할 때 사용
* Query: 반환 타입이 명확하지 않을때 사용


``` java
  
  TypedQuery<Member> query1 = em.createQuery("select m from Member m", Member.class);
  
  TypedQuery<String> query2 = em.createQuery("select m.username from Member m", String.class);
  
```

``` java
  Query query3 = em.createQuery("select m.username, m.age from Member m");
```

## 결과 조희 API

* query.getResultList(): **결과가 하나 이상일 때, 리스트 반환
  * 결과가 없으면 빈 리스트 반환

``` java
  TypedQuery<Member> query1 = em.createQuery("select m from Member m", Member.class);

  List<Member> resultList = query1.getResultList();

  for (Member member1 : resultList) {
     System.out.println("member1 = " + member1);
  }
  
```  
  
* query.getSingleResult(): **결과가 정확히 하나**, 단일 객체 반환
  * 결과가 없으면: javax.persistence.NoResultException
  * 둘 이상이면: javax.persistence.NonUniqueResultException

``` java
  
  Query query2 = em.createQuery("select m.username, m.age from Member m");
  Object singleResult = query2.getSingleResult();
  
  System.out.println("singleResult = " + singleResult);
  
```  

## 파라미터 바인딩 - 이름 기준, 위치 기준

* 이름 기준으로 바인팅 할것
* 위치 기준은 중간에 순서가 끼어들면 장애로 이어짐


* 이름 기준 
``` java

 TypedQuery<Member> query = em.createQuery("select m from Member m where m.username = :username", Member.class);
 query.setParameter("username", "member1");
 
 Member result = query.getSingleResult();

``` 

* 위치 기준

``` java

 select m form Member m where m.username=?1
 
 query.setParameter(1, usernameParam);

``` 

## 프로젝션

* SELECT 절에 조회할 대상을 지정하는 것
* 프로젝션 대상: 엔티티, 임베디드 타입, 스칼라 타입(숫자, 문자등 기본 데이터 타입)
* SELECT **m** FROM Member m -> 엔티티 프로젝션
``` java

 // 영속성 컨텍스트에서 관리 된다.
 List<Member> result = em.createQuery("select m from Member m ", Member.class).getResultList();

 Member findMember = result.get(0);
 findMember.setAge(20);

```
  
  * 조인 쿼리가 나가는데, 다른 사람도 예측할 수 있게 왠만하면 JPQL에 조인을 명시하는 것이 좋다.(뒤에서 배움)
  * join 빼도 알아서 해줌

``` java

 List<Team> result2 = em.createQuery("select m.team from Member m join m.team t", Team.class).getResultList();

```

* SELECT **m.address** FROM Member m -> 임베디드 타입 프로젝션

  * 소속된 엔티티에서 부터 시작되어야 한다.

``` java

 em.createQuery("select o.address from Order o", Address.class).getResultList();

```

* SELECT **m.username, m.age** FROM Member m -> 스칼라 타입 프로젝션

``` java

 em.createQuery("select m.username, m.age from Member m").getResultList();

```

* DISTINCT로 중복 제거

``` java

 em.createQuery("select distinct m.username, m.age from Member m").getResultList();

```

## 프로젝션 - 여러 값 조희

***고민거리: 응답 타입이 2개데 어떻케 가져오지?***

* SELECT **m.username, m.age** FROM Member m

* Query 타입으로 조회
  * 타입을 지정하지 못해서 오브젝트로 돌려줌 그래서 타입 캐스팅 해줘여됨 

``` java

 List resultList = em.createQuery("select m.username, m.age from Member m").
           getResultList();

 Object o = resultList.get(0);
 Object[] result = (Object[]) o;
 
 System.out.println("username = " + result[0]);
 System.out.println("age = " + result[1]);

```

* Object[] 타입으로 조회
  * 위에 Query 타입 조회와 몇줄빠지는거 말고ㅍ 별차이 없음

``` java

 List<Object> resultList1 = em.createQuery("select m.username, m.age from Member m").
                    getResultList();

 Object[] result1 = (Object[]) resultList1.get(0);

 System.out.println("username = " + result1[0]);
 System.out.println("age = " + result1[1]);

```

* new 명령어로 조회
  * 단순 값을 DTO로 바로 조회  SELECT **new** jpabook.jpql.UserDTO(m,username, m.age) FROM Member m
  * 패키지 명을 포함한 전체 클래스 명 입력
  * 순서와 타입이 일치하는 생성자 필요
  * **나중에 QueryDSL로 극복할 수 있다.**

``` java

  List<MemberDTO> members =
  em.createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
           .getResultList();



  System.out.println("username = " + members.get(0).getUsername());
  System.out.println("age = " + members.get(0).getAge());

```

## 페이징 API

* JPA는 페이징을 다음 두 API로 추상화

* 조회 시작 위치(0부터 시작)

``` java

  setFirstResult(int startPosition)
  
```

* 조회할 데이터 수

``` java

  setMaxResults(int maxResult)
  
```

* 페이징 API 예시


``` java

  List<Member> result = em.createQuery("select m from Member m order by m.age desc", Member.class)
                    .setFirstResult(1)
                    .setMaxResults(10)
                    .getResultList();
  
```

* 페이지 API - MySQL 방언

``` sql

  SELECT
      M.ID AS ID,
      M.AGE AS AGE,
      M.TEAM_ID AS TEAM_ID,
      M.NAME AS NAME
  FROM
      MEMBER M
  ORDER BY
      M.name DESC LIMIT ?, ?

```

* 페이지 API - Oracle 방언

``` sql

  SELECT * FROM
      ( SELECT ROW_.*, ROWNUM ROWNUM_
      FROM
          ( SELECT
              M.ID AS ID,
              M.AGE AS AGE,
              M.TEAM_ID AS TEAM_ID,
              M.NAME AS NAME
            FROM MEMBER M
            ORDER BY M.NAME
           ) ROW_
       WHERE ROWNUM <= ?
       )
  WHERE ROWNUM_ > ?

```

### 조인

* 내부 조인

``` sql
SELECT m
FROM Member m
[INNER] JOIN m.team t
```

* 외부 조인

``` sql
SELECT m
FROM MEMBER m
LEFT [OUTER] JOIN m.team t
```

* 세타 조인(막조인)

``` sql
SELECT COUNT(m)
FROM Member m, Team t
WHERE m.username = t.name
```

### 조인 - ON 절

- JPA2.1 부터 ON절을 활용한 조인이 가능하다.

  - 조인시 조인 대상을 미리 필터링 할 수 있음
  - 하이버네이트 5.1부터 **연관관계가 없는 엔티티도 외부 조인**이 가능!

## **1. 조인 대상 필터링**

* 예) 회원과 팀을 조인하면서, 팀 이름이 A인 팀만 조인

* JPQL:

```sql
  SELECT m,t 
  FROM Member m LEFT JOIN m.team t on t.name = 'A'
```

* SQL:

```sql
  SELECT m.*, t.*
  FROM Member m LEFT JOIN TEAM t ON m.TEAM_ID = t.id AND t.name = 'A'
```

## **2. 연관관계 없는 엔티티 외부 조인**

* 예) 회원의 이름과 팀의 이름이 같은 대상 외부 조인. 연관관계가 없다.

* 서로 아무 연관관계가 없어도 LEFT JOIN 가능하다.


* JPQL:

```sql
    SELECT m,t 
    FROM Member m 
    LEFT JOIN Team t
      on m.username = t.name
```

* SQL:

```sql
    SELECT m.*, t.*
    FROM Member m
    LEFT JOIN TEAM t
      ON m.username = t.name
```

## 서브 쿼리

* 나이가 평균보다 많은 회원

  * 메인쿼리와 서브쿼리 서로 관계가 없다.  m, m2 보통 이렇케 짜야 성능이 잘나옴.

```sql
    select m form Member m
    where m,age > (select avg(m2.age) from Member m2)
```


* 한 건이라도 주문한 고객

```sql
    select m form Member m
    where (select count(o) from Order o where m = o.member) > 0
```

## 서브 쿼리 지원 함수

* [NOT] EXISTS (subquery): 서브쿼리에 결과가 존재하면 참
  * {ALL | ANY | SOME} (subquery)
  * ALL 모두 만족하면 참
    * ANY, SOME: 같은 의미, 조건을 하나라도 만족하면 참

* [NOT] IN (subquery): 서브쿼리의 결과 중 하나라도 같은 것이 있으면 참


## 서브 쿼리 - 예제

* 팀 A 소속인 회원

```sql
    select m form Member m
    where exists (select t from m.team t where t.name = '팀A')
```

* 전체 상품 각각의 재고보다 주문량이 많은 주문들

```sql
    select o from Order
    where o.orderAmount > ALL (select p.stockAmount from Product p)
```

* 어떤 팀이든 팀에 소속된 회원

```sql
    select m form Member m
    where m.team = ANY (select t from Team t)
```

## JAP 서브 쿼리 한계

* JPA는 WHERE , HAVING 절에서만 서브 쿼리 사용 가능

* SELECT 절도 가능(하이버네이트에서 지원)

* **FROM 절의 서브 쿼리는 현재 JPQL에서 불가능**
  * 조인으로 풀 수 있으면 풀어서 해결
  * 그래도 쓰고 싶다면
    * 네이티브 쿼리로 해결하거나,
    * 어플리케이션 비즈니스 로직에서 풀어내거나
    * 쿼리를 두번 날려서 해결할 수 있다.

## JPQL 타입 표현

* 문자
  * 'HELLO', 'She''s'

* 숫자
  * 10L(Long), 10D(Double), 10F(Float)

* Boolean
  * TRUE, FALSE

* ENUM
  * Jpabook.MemberType.Admin(풀 패키지명 포함)
  * 보통은 파라미터 바인딩으로 처리
  * QueryDSL에서는 복잡하지 않게 자바 코드로 사용한다.

* 예시

``` java

   String query = "select m.username, 'HELLO', true from Member m " +
   "where m.type = :userType";

   List<Object[]> result = em.createQuery(query)
        .setParameter("userType", MemberType.ADMIN)
        .getResultList();
        
   for (Object[] objects : result) {
        System.out.println("objects[0] = " + objects[0]);
        System.out.println("objects[1] = " + objects[1]);
        System.out.println("objects[2] = " + objects[2]);
   }

``` 

* 엔티티 타입
  * TYPE(m) = Member (상속 관계에서 사용)
  * Item을 상속받은 여러개의 클래스 중 Book만 조회하고 싶은 경우

``` java

  em.createQuery("select i from Item i where type(i) = Book", Item.class).getResultList();

``` 

## JPQL 기타

* SQL과 문법이 같은  식
* EXISTS, IN
* AND, OR, NOT
* =, >, >=, <, <=, <>
* BETWEEN, LIKE, **IS NULL**

## CASE 식

* 기본 CASE 식

    ```sql
    select
        case when m.age <= 10 then '학생요금'
             when m.age >= 60 then '경로요금'
             else '일반요금'
        end 
    from Member m
    ```
  
``` java
    String query =
            "select " +
            "case when m.age <= 10 then '학생요금' " +
            "     when m.age >= 60 then '경로요금' " +
            "     else '일반요금' " +
            "end " +
            "from Member m";

    List<String> result = em.createQuery(query, String.class).getResultList();

    for (String s : result) {
         System.out.println("s = " + s);

    }        
```

- 단순 CASE 식

    ```sql
    select
        case t.name
          when '팀A' then '인센티브110%'
          when '팀B' then '인센티브120%'
          else '인센티브105%'
        end
    from Team t
    ```

## 조건식 - CASE 식

* COALESCE: 하나씩 조회해서 null아 아니면 반환
* NULLIF: 두 값이 같으면 null 반환, 다르면 첫번째 값 반환


* 사용자 이름이 없으면 이름 없는 회원을 반환

    ```sql
    
    select coalesce(m.username, '이름 없는 회원') from Member m
    
    ```

* 사용자 이름이 '관리자'면 null을 반환하고 나머지는 본인의 이름을 반환

    ```sql
    
    select NULLIF(m.username, '관리자') from Member m
    
    ```

## JPQL 기본 함수

* JPQL이 제공하는 표준 함수

- CONCAT

- SUBSTRING

- TRIM

- LOWER, UPPER

- LENGTH

- LOCATE -> indexOf와 같이 자리수 반환

- ABS, SQRT, MOD

- SIZE, INDEX(JPA 용도)


## 사용자 정의 함수 호출

* 하이버네이트는 사용전 방언에 추가해야 한다.

  * 사용하는 DB 방언을 상속받고, 사용자 정의 함수를 등록한다.

```sql

    select function ('group_concat', i.name) from Item i
  
```


### Reference

- [자바 ORM 표준 JPA 프로그래밍](https://www.inflearn.com/course/ORM-JPA-Basic)
