# JPA 객체지향 쿼리 언어 - 중급문법

## 경로 표현식

* .(점)을 찍어 객체 그래프를 탐색하는 것

``` sql
    select m.username -> 상태 필드
      from Member m
      join m.team t   -> 단일 값 연관 필드
      join m.orders o -> 컬렉션 값 연관 필드
     where t.name = '팀A'
```

### 경로 표현식 용어 정리

* 상태 필드(state field)
  * **단순히 값을 저장** 하기 위한 필드(ex: m.username)
* 연관 필드(association field)
  * 연관관계를 위한 필드
  * **단일 값 연관 필드**
    * @ManyToOne, @OneToOne, **대상이 엔티티** (ex: m.team)
  * **컬렉션 값 연관 필드**
    * @OneToMany, @ManyToMany, **대상이 컬렉션** (ex: m.orders)

### 경로 표현식 특징

* **상태 필드**(state field): 경로 탐색의 끝, 탐색X
* **단일 값 연관 경로**: 묵시적 내부 조인(inner join) 발생, 탐색O
  
  * 위험한 녀석, 실무에서 쓰기 어려움 

``` java
    String query = "select m.team from Member m";

    List<Team> result = em.createQuery(query, Team.class).getResultList();
```   

``` sql

  Hibernate: 
    /* select
        m.team 
    from
        Member m */ select
            team1_.TEAM_ID as team_id1_3_,
            team1_.name as name2_3_ 
        from
            Member member0_ 
        inner join
            Team team1_ 
                on member0_.TEAM_ID=team1_.TEAM_ID

``` 

    * 묵시적 내부 조인 발생. 하지만, 더 이상 탐색할 수 없다.

        * t.members로 경로 표현하는 순간 컬렉션이다. 컬렉션 자체이므로 필드에 접근하거나 더 이상 탐색할 수 없다. 대신에 컬렉션의 메서드인 size()는 사용 가능하다.


``` java
    String query = "select t.members from Team t";
            
    Collection result = em.createQuery(query, Collection.class).getResultList();
    
```

* **컬렉션 값 연관 경로**: 묵시적 내부 조인 발생, 탐색X
  * 묵시적으로 발생한 내부 조인 쿼리

``` sql
Hibernate: 
    /* select
        t.members 
    from
        Team t */ select
            members1_.MEMBER_ID as member_i1_0_,
            members1_.age as age2_0_,
            members1_.TEAM_ID as team_id5_0_,
            members1_.type as type3_0_,
            members1_.USERNAME as username4_0_ 
        from
            Team team0_ 
        inner join
            Member members1_ 
                on team0_.TEAM_ID=members1_.TEAM_ID   
                        
```


* FROM 절에서 명시적 조인을 통해 별칭을 얻으면 별칭을 통해 탐색 가능
* 값 타입 컬렉션에 컬렉션의 위치 값을 구할 때 @OrderColumn 사용. INDEX로 위치 참조 가능

``` java
    String query = "select m.username from Team t join t.members m";

    List<String> result = em.createQuery(query, String.class).getResultList();
```

``` sql
Hibernate: 
    /* select
        m.username 
    from
        Team t 
    join
        t.members m */ select
            members1_.USERNAME as col_0_0_ 
        from
            Team team0_ 
        inner join
            Member members1_ 
                on team0_.TEAM_ID=members1_.TEAM_ID
```

* 이해하기
  * 묵시적 조인 실무에서 X
  * 명시적 조인 사용해야함 

### 명시적 조인, 묵시적 조인

* 명시적 조인: join 키워드 직접 사용
  * select m from Member m **join m.team t**


* 무시적 조인: 경로 표현식에 의해 묵시적으로 SQL 조인 발생  (내부(inner) 조인만 가능, 외부조인 X)
  * select **m.team** from Member m 

* 경로 표현식 - 예제
* select o.member.team from Order o -> 성공
* select t.members from Team -> 성공
* select t.members.username from Team t -> 실패
* select m.username from Team t join t.members m -> 성공

### 실무 조언

* **가급적 묵시적 조인 대신에 명시적 조인 사용**
* 조인은 SQL 튜닝에 중요 포인트
* 묵시적 조인은 조인이 일이나는 상황을 한눈에 파악하기 어려움!

## JPQL - 페치 조인(fetch join)
* **실무에서 정말정말 중요함!!**

### 페치 조인(fetch join)

* SQL 조인 종류 X
* JPQL에서 **성능 최적화**를 위해 제공하는 기능
* 연관된 엔티티나 컬렉션을 **SQL 한 번에 함께 조회**하는 기능
* join fetch 명령어 사용
* 페치 조인 :: [ LEFT [OUTER] | INNER ] JOIN FETCH 조인경로

### 엔티티 페치 조인

* 회원을 조회하면서 연관된 팀도 함께 조회(SQL 한 번에)
* SQL을 보면 회원 뿐만 아니라 **팀(T.*)**도 함께 **SELECT**

* [JPQL]
```sql
  select m from Member m join fetch m.team
```

* [SQL]
```sql
  SELECT M.*, T.* FROM MEMBER M 
  INNER JOIN TEAM T ON M.TEAM_ID = T.ID
```

### 예제

* fetch join 없이하면 N + 1 발생 (쿼리가 여러번 나감)
* fetch join 사용시 지연 로딩을 하지 않고, 회원과 팀을 한번에 조회한다.
* 지연로딩으로 해도 fetch join이 우선시 됨


``` java

  //String query = "select m from Member m";
  String query = "select m from Member m join fetch m.team";

  List<Member> result = em.createQuery(query, Member.class).getResultList();

  for (Member member : result) {
       System.out.println("member = " + member.getUsername() + "," + member.getTeam().getName());
       //회원1, 팀A(SQL)
       //회원2, 팀A(1차 캐시)
       //회원3, 팀B

       //회원 100명 -> N + 1 (fetch 조인으로 해결)
  }
```  

### 컬렉션 페치 조인

* 일대다 관계, 컬렉션 페치 조인

* [JPQL]
``` sql

  select t
  from Team t join fetch t.members
  where t.name = '팀A'

```  

* [SQL]
``` sql

  SELECT T.*, M.*
  FROM TEAM T
  INNER JOIN MEMBER M ON T.ID = M.TEAM_ID
  WHERE T.NAME = '팀A'

```

![](https://github.com/dididiri1/jpabook/blob/main/images/11_01.png?raw=true)

### 페치 조인과 DISTINCT

* SQL의 DISTINCT는 중복된 결과를 제거하는 명령
* JPQL의 DISTINCT 2가지 기능 제공
  * 1. SQL에 DISTINCT를 추가
  * 2. 애플리케이션에서 엔티티 중복 제거

* SQL 쿼리에서는 DISTINCT를 추가 하지만 데이터가 다르면 안됨, 완전이 컬럼값이 똑같에야 중복 제거함.
* DISTINCT가 추가로 애플리케이션에서 중복 제거시도
* 같은 식별자를 가진 **Team 엔티티 제거**

![](https://github.com/dididiri1/jpabook/blob/main/images/11_02.png?raw=true)


### 페치 조인과 일반 차이의 차이

* 일반 조인 실행시 연관된 엔티티를 함께 조회하지 않음
* JPQL은 결과를 반환할 때 연관관계 고려X
* 단지 SELECT 절에 지정한 엔티티만 조회할 뿐
* 여기서는 팀 엔티티만 조회하고, 회원 엔티티는 조회X
* 페치 조인을 사용할 때만 연관된 엔티티도 함께 **조회(즉시 로딩)**
* **페치 조인은 객체 그래프를 SQL 한번에 조회하는 개념**