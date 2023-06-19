
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

### 예제

- Parent

``` java

  @Entity
  @Getter @Setter
  public class Parent {
  
      @Id @GeneratedValue
      private Long id;
  
      private String name;
  
      @OneToMany(mappedBy = "parent")
      private List<Child> childList = new ArrayList<>();
  
      public void addChild(Child child) {
          childList.add(child);
          child.setParent(this);
      }
  }

```

- Child

``` java

  @Entity
  @Getter @Setter
  public class Child {
  
      @Id @GeneratedValue
      private Long id;
  
      private String name;
  
      @ManyToOne
      @JoinColumn(name = "parent_id")
      private Parent parent;
  }
  
```

``` java
  public static void main(String[] args) {

      EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
      
      ...

      try{
          Child child1 = new Child();
          Child child2 = new Child();

          Parent parent = new Parent();
          parent.addChild(child1);
          parent.addChild(child2);

          em.persist(parent);
          em.persist(child1);
          em.persist(child2);

          tx.commit();
          
      } catch (Exception e) {
      
      ...
  }
``` 

- cascade 추가
  - 추가시 Parent만 persist 하면 자식은 전이 된다.


``` java
  
  @Entity
  @Getter
  @Setter
  public class Parent {
  
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;
  
      private String name;
  
      @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL) //cascade 추가
      private List<Child> children = new ArrayList<>();
  
      public void addChild(Child child) {
          this.children.add(child);
          child.setParent(this);
      }
  }
  
``` 

### 영속성 전이: CASCADE - 주의!

- 영속성 전이는 연관관계를 매핑하는 것과 아무 관련이 없음
- 엔티티를 영속화할 때 연관된 엔티티도 함께 영속화하는 편리함 을 제공할 뿐

- CASCADE의 종류
  - **ALL: 모두 적용**
  - **PERSIST: 영속**
  - **REMOVE: 삭제**
  - MERGE: 병합
  - REFRESH: REFRESH
  - DETACH: DETACH

## 고아 객체

### 고아 객체

- 고아 객체 제거:부모 엔티티와 연관관계가 끊어진 자식 엔티티를 자동으로 삭제

- **orphanRemoval = true**

``` java

  @Entity
  @Getter @Setter
  public class Parent {

    ...

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Child> childList = new ArrayList<>();

    public List<Child> getChildList() {
        return childList;
    }

    ...
}

``` 

``` java

  Child child1 = new Child();
  Child child2 = new Child();

  Parent parent = new Parent();
  parent.addChild(child1);
  parent.addChild(child2);

  em.persist(parent);

  em.flush();
  em.clear();

  Parent findParent = em.find(Parent.class, parent.getId());
  findParent.getChildList().remove(0);
  
``` 

``` log
Hibernate: 
    select
        parent0_.id as id1_1_0_,
        parent0_.name as name2_1_0_ 
    from
        Parent parent0_ 
    where
        parent0_.id=?
Hibernate: 
    select
        childlist0_.parent_id as parent_i3_0_0_,
        childlist0_.id as id1_0_0_,
        childlist0_.id as id1_0_1_,
        childlist0_.name as name2_0_1_,
        childlist0_.parent_id as parent_i3_0_1_ 
    from
        Child childlist0_ 
    where
        childlist0_.parent_id=?
Hibernate: 
    /* delete hellojpa.Child */ delete 
        from
            Child 
        where
            id=?
``` 

- delete from Child where id = ?

### 고아 객체 - 주의

- 참조가 제거된 엔티티는 다른 곳에서 참조하지 않는 고아 객체  
  보고 삭제하는 기능
- **참조하는 곳이 하나일 때 사용해야함!**
- **특정 엔티티가 개인 소유할 때 사용**
- @OneToOne, @OneToMany만 가능

> 참고: 개념적으로 부모를 제거하면 자식은 고아가 된다. 따라서 고아  
> 객체 제거 기능을 활성화 하면, 부모를 제거할 때 자식도 함께 제거된다. 
> 이것은 CascadeType,REMOVE 처럼 동작한다.

``` java

  @Entity
  @Getter @Setter
  public class Parent {

    ...

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Child> childList = new ArrayList<>();

    public List<Child> getChildList() {
        return childList;
    }

    ...
}

``` 

``` java
  Child child1 = new Child();
  Child child2 = new Child();

  Parent parent = new Parent();
  parent.addChild(child1);
  parent.addChild(child2);

  em.persist(parent);
  em.persist(child1);
  em.persist(child2);

  em.flush();
  em.clear();

  Parent findParent = em.find(Parent.class, parent.getId());

  em.remove(findParent);
``` 

-  부모인 Parent 지우고. 자식 Child도 지운다.

``` log
  Hibernate: 
    /* delete hellojpa.Child */ delete 
        from
            Child 
        where
            id=?
  Hibernate: 
      /* delete hellojpa.Child */ delete 
          from
              Child 
          where
              id=?
  Hibernate: 
      /* delete hellojpa.Parent */ delete 
          from
              Parent 
          where
              id=?
``` 

### 영속성 전이 + 고아 객체, 생명주기

- **CascadeType.ALL + orphanRemovel = true**
- 스스로 생명주기를 관리하는 엔티티는 em.persist()로 영속화, em.remove()로 제거
- 두 옵션을 모두 활성화 하면 부모 엔티티를 통해서 자식의 생명주기를 관리할 수 있음
- 도메인 주도 설계(DDD)의 Aggregate Root 개념을 구현할 때 유용