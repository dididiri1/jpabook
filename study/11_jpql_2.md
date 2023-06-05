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

## 명시적 조인, 묵시적 조인

* 명시적 조인: join 키워드 직접 사용
  * select m from Member m **join m.team t**


* 무시적 조인: 경로 표현식에 의해 묵시적으로 SQL 조인 발생  (내부(inner) 조인만 가능, 외부조인 X)
  * select **m.team** from Member m 

* 경로 표현식 - 예제
* select o.member.team from Order o -> 성공
* select t.members from Team -> 성공
* select t.members.username from Team t -> 실패
* select m.username from Team t join t.members m -> 성공

## 실무 조언

* **가급적 묵시적 조인 대신에 명시적 조인 사용**
* 조인은 SQL 튜닝에 중요 포인트
* 묵시적 조인은 조인이 일이나는 상황을 한눈에 파악하기 어려움!