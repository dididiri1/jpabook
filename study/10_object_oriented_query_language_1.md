# 객체지향 쿼리 언어1

## JPA는 다양한 쿼리 방법을 지원

* **JPQL**
* JPA Criteria
* **QuertDSL**
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

* 문자가 아닌 자바코드롤 JPQL을 작성할 수 있음
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

* JJPQL은 엔티티 객체를 대상으로 쿼리 를 질의하고
  * JSQL은 데이터베이스 테이블을 대상으로 쿼리 를 질의한다.
  * JJPQL은 SQL을 추상화해서 특정 데이터베이스 SQL에 의존하지 않는다.

* JJPQL은 결국 SQL로 변환된다.


![](https://github.com/dididiri1/jpabook/blob/main/images/10_01.png?raw=true)

### JPQL 문법

* select m form **Member** as m where **m.age** > 18
* 엔티티와 속성은 대소문자 구분O (Mmember, age)
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

``` java

 TypedQuery<Member> query = em.createQuery("select m from Member m where m.username = :username", Member.class);
 query.setParameter("username", "member1");
 Member result = query.getSingleResult();

``` 