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


### 객체 지향 모델링 (객체 연관관계 사용)

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

### 객체 지향 모델링 (ORM 매핑)

![](https://github.com/dididiri1/jpabook/blob/main/images/05_03.png?raw=true)

### 객체 지향 모델링 (연관관계 저장)

- member.setTeam(team)
- Team 객체를 그대로 넣어버린다.

``` java
  //팀 저장
  Team team = new Team();
  team.setName("TeamA");
  em.persist(team);
  
  //회원 저장
  Member member = new Memeber();
  member.setName("member1");
  member.setTeam(team);    //단방향 연관관계 설정, 참조 저장
  em.persist(member);
  
  Member findMember = em.find(Member.class,  member.getId());
```

### 객체 지향 모델링 (참조로 연관관계 조회 - 객체 그래프 탐색)

``` java
  //조회
  Member findMember = em.find(Member.class, member.getId()); 
  
  //참조를 사용해서 연관관계 조회
  Team findTeam = findMember.getTeam();
```

### 객체 지향 모델링 (연관관계 수정)

``` java
 // 새로운 팀B
 Team teamB = new Team();
 teamB.setName("TeamB");
 em.persist(teamB);
 // 회원1에 새로운 팀B 설정
 member.setTeam(teamB);

```

### 양방향 매핑

![](https://github.com/dididiri1/jpabook/blob/main/images/05_04.png?raw=true)

### 양방향 매핑 (Member 엔티티는 단방향과 동일)

``` java
 @Entity
 public class Member { 
 
     @Id @GeneratedValue
     private Long id;
     @Column(name = "USERNAME")
     private String name;
     private int age;
     @ManyToOne
     @JoinColumn(name = "TEAM_ID")
     private Team team
     
 }
```

### 양방향 매핑  (Team 엔티티는 컬렉션 추가)

``` java
 @Entity
 public class Team {
 
     @Id @GeneratedValue
     private Long id;
     
     private String name;
     
     @OneToMany(mappedBy = "team")
     List<Member> members = new ArrayList<Member>();
 }
```

### 양방향 매핑  (반대 방향으로 객체 그래프 탐색)

``` java
  //조회
  Team findTeam = em.find(Team.class, team.getId()); 
  
  int memberSize = findTeam.getMembers().size(); //역방향 조회

``` 

### 연관관계의 주인과 mappedBy

- mappedBy = JPA의 멘탈붕괴 난이도
- mappedBy는 처음에는 이해하기 어렵다.
- 객체와 테이블간에 연관관계를 맺는 차이를 이해해야 한다.

### 객체와 테이블이 관계를 맺는 차이

- **객체 연관관계 = 2개**
  - 회원 -> 팀 연관관계 1개(단방향)
  - 팀 -> 회원 연관관계 1개(단방향)
- **테이블 연관관계 = 1개**
  - 회원 <-> 팀의 연관관계 1개(양방향)


- 객체와 테이블이 관계를 맺는 차이

![](https://github.com/dididiri1/jpabook/blob/main/images/05_05.png?raw=true)

### 객체의 양방향 관계

- 객체의 **양방향 관계는 사실 양방향 관계가 아니라 서로 다른 단 뱡향 관계 2개다.**
- 객체를 양방향으로 참조하려면 **단방향 연관관계를 2개** 만들어야 한다. 

### 테이블의 양방향 연관관계

- 테이블은 **외래 키 하나**로 두 테이블의 연관관계를 관리
- MEMBER.TEAM_ID 외래 키 하나로 양방향 연관관계 가짐  
  (양쪽으로 조인할 수 있다.)

``` sql
  SELECT * 
  FROM MEMBER M
  JOIN TEAM T ON M.TEAM_ID = T.TEAM_ID
``` 

``` sql
  SELECT * 
  FROM TEAM T
  JOIN MEMBER M ON T.TEAM_ID = M.TEAM_ID
``` 
