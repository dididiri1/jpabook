# 연관관계 매핑 가치

### 목표
- **객체와 테이블 연관관계의 차이를 이해**
- **객체의 참조와 테이블의 외래 키를 맵핑
- 용어 이해
  - **방향**(Direction): 단방향, 양방향
  - **다중성**(Multiplicity): 다대일(N:1), 일대다(1:N), 일대일(1:1), 다대다(N:M) 이해

- **연관관계의 주인**(Owner): 객체 양방향 연관관계는 관리주인이 필요

![](https://github.com/dididiri1/jpabook/blob/main/images/05_01.png?raw=true)

```java
  @Entity
  public class Member {
    
      @Id
      @GeneratedValue
      private Long id;
      
      @Column(name = "USERNAME")
      private String name;
      
      private int age;
      
      @Column(name = "TEAM_ID")
      private Long teamId;

  }
```

```java
  @Entity
  public class Team {
     
      @Id
      @GeneratedValue
      private Long id;
      
      private String name;

    }
```

- 객체를 테이블에 맞추어 모델링
  - 식별자로 다시 조회, 객체 지향적인 방법은 아니다.

``` java
  //조회
  Member findMember = em.find(Member.class, member.getId());
        
  //연관관계가 없음
  Team findTeam = em.find(Team.class, team.getId());
```

- 객체를 테이블에 맞추어 데이터 중심으로 모델링하면, 협력 관계를 만들 수 없다.

  - **테이블은 외래 키로 조인**을 사용해서 연관된 테이블을 찾는다.
  - **객체는 참조**를 사용해서 연관된 객체를 찾는다.
  - 테이블과 객체 사이에는 이런 큰 간격이 존재한다.


- 객체 지향 모델링 (객체 연관관계 사용)

![](https://github.com/dididiri1/jpabook/blob/main/images/05_02.png?raw=true)

``` java
  @Entity
  public class Member {
  
      @Id
      @GeneratedValue
      private Long id;
      
      @Column(name = "USERNAME")
      private String name;
      
      private int age;

      @ManyToOne
      @JoinColumn(name = "TEAM_ID")
      private Team team;

    }
```