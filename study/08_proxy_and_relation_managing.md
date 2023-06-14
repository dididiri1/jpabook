
# 프록시와 연관관계 관리

> 프록시  
> 즉시 로딩과 지연 로딩  
> 영속성 전이: CASCADE  
> 고아 객체  
> 영속성 전이 + 고아 객체, 생명주기  
> 실전 예저 - 5. 연관관계 관리

## 프록시

![](https://github.com/dididiri1/jpabook/blob/main/images/08_01.png?raw=true)

### Member를 조회할 때 Team도 함께 조히해야 할까?

- 회원과 팀 함께 출력
``` java
 public void printUserAndTeam(String memberId) {
    Member member = em.find(Member.class, memberId);
    Team team = member.getTeam();
    
    System.out.println("회원 이름: " + member.getUsername());
    System.out.println("소속팀: " + team.getName());
 }
```

- 회원만 출력
``` java
 public void printUserAndTeam(String memberId) {
    Member member = em.find(Member.class, memberId);
    Team team = member.getTeam();
    
    System.out.println("회원 이름: " + member.getUsername());
 }
```

![](https://github.com/dididiri1/jpabook/blob/main/images/08_03.png?raw=true)



![](https://github.com/dididiri1/jpabook/blob/main/images/08_04.png?raw=true)



### 프록시 특징

- 프록시 객체는 실제 객체의 참조(targer)를 보관
- 프록시 객체를 호출하면 프록시 객체는 실제 객체의 메소드 호출

![](https://github.com/dididiri1/jpabook/blob/main/images/08_05.png?raw=true)

### 프록시 객체 초기화

``` java
 Member member = em.getReference(Member.class, 1L);
 member.getName();
```
![](https://github.com/dididiri1/jpabook/blob/main/images/08_06.png?raw=true)

### 프록시 특징 정리

- 프록시 객체는 처음 사용할 때 한 번만 초기화

- 프록시 객체를 초기화 할 때, 프록시 객체가 실제 엔티티로 바뀌는 것은 아님, 초기화되면 프록시 객체를 통해서 실제 엔티티에 접근 가능

- 영속성 컨텍스트에 찾는 엔티티가 이미 있으면 em.**getReference()**를 호출해도 실제 엔티티 반환

- 영속성 켄텍스트의 도움을 받을 수 없는 준영속 상태일 때, 프록시를 초기화하면 문제 발생  
  (하이버네이트는 org.hibernate.LazyInitializationException 예외를 터트림)


### 프록시 확인

- 프록시 인스턴스의 초기화 여부 확인
    - PersistenceUnitUtil.isLoaded(Object entity)

- 프록시 클래스 확인 방법
    - entity.getClass().getName() 출력 (..javasist.. or HibernateProxy...)

- 프록시 강제 초기화
    - org.hibernate.Hibernate.initialize(entity);

- 참고: JPA 표준은 강제 초기화 없음
    - 강제 호출: **member.getName()**

## 즉시 로딩과 지연 로딩

### 지연 로딩 LAZY을 사용해서 프록시로 조회

``` java
  @Entity
  public class Member  {
  
      @Id
      @GeneratedValue
      private Long id;
  
      @Column(name = "username")
      private String username;
      
      @ManyToOne(fetch = FetchType.LAZY)
      @JoinColumn(name = "team_id")
      private Team team;
      
      ...
  }
```

### 즉시 로딩

![](https://github.com/dididiri1/jpabook/blob/main/images/08_07.png?raw=true)

- 즉시 로딩(EAGER), Member조회시 항상 Team도 조회

### 프록시와 즉시로딩 주의

- **가급적 지연 로딩만 사용(특히 실무에서)**
- 즉시 로딩을 적용하면 예상하지 못한 SQL이 발생
- **즉시 로딩은 JPQL에서 N+1 문제를 일으킨다.**
- **@ManyToOne, @OneToOne은 기본이 즉시 로딩임<br>-> LAZY로 항상 설정해야됨**
- @OneToMany, @ManayToMany는 기본이 지연 로딩
  
### 지연 로딩 활용

- **Member**와 **Team**은 자주 함께 사용 -> **즉시 로딩**
- **Member**와 **Order**는 가끔 사용 -> **지연 로딩**
- **Order**와 **Product**는 자주 함께 사용 -> **즉시 로딩**

![](https://github.com/dididiri1/jpabook/blob/main/images/08_08.png?raw=true)

![](https://github.com/dididiri1/jpabook/blob/main/images/08_09.png?raw=true)

![](https://github.com/dididiri1/jpabook/blob/main/images/08_10.png?raw=true)

### 지연 로딩 활용 - 실무

- 모든 연관관계에 지연 로딩을 사용해라!
- 실무에서 즉시 로딩을 사용하지 마라!
- JPQL fetch 조인이나, 엔티티 그래프 기능을 사용해라!  
  (뒤에서 설명)
- 즉시 로딩은 상상하지 못한 쿼리가 나간다.

### 영속성 전이: CASCADE

- 특정 엔티티를 영속 상태로 만들 때 연관된 엔티티도 함께 영속  
  상태로 만들도 싶을 때
- 예: 부모 엔티티를 저장할 때 자식 엔티티도 함께 저장.

![](https://github.com/dididiri1/jpabook/blob/main/images/08_11.png?raw=true)

``` java

  @OneToMany(mappedBy="parent", cascade=CascadeType.PERSIST)
  
```

