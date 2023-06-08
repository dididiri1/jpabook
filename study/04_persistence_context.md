


## 영속성 컨텍스트

* JPA를 이해하는데 가장 중요한 용어
* "엔티티를 영구 저장하는 환경"이라는 뜻
* **EntityManager.persist(entity);**


## 엔티티 매니저? 영속성 컨텍스트?

* 영속성 컨텍스트는 논리적인 개념
* 눈에 보이지 않는다.
* 엔티티 매니저를 통해서 영속성 컨텍스트에 접근

- **비영속(new/transient)**
  - 영속성 컨텍스트와 전혀 관계가 없는 상태

``` java
  // 객체를 생성만 한 상태(비영속)
  Member member = new Member();
  member.setId(100L);
  member.setUsername("HelloJPA");
```

- **영속(managed)**
  - 영속성 컨텍스트에 저장된 상태
  - 엔티티가 영속성 컨텍스트에 의해 관리된다.
  - 이때 DB에 저장 되지 않는다. 영속 상태가 된다고 DB에 쿼리가 날라가지 않는다.
  - 트랜잭션의 커밋 시점에 영속성 컨텍스트에 있는 정보들이 DB에 쿼리로 날라간다.

``` java
   // 객체를 생성한 상태(비영속)
   Member member = new Member();
   member.setId(100L);
   member.setUsername("HelloJPA");
      
   EntityManager em = emf.createEntityManager();
   em.getTransaction().begin();
      
   // 객체를 저장한 상태(영속)태
   em.persist(member);
```

- **준영속(detached)**

  - 영속성 컨텍스트에 저장되었다가 분리된 상태

``` java
   // 회원 엔티티를 영속성 컨텍스트에서 분리, 준영속 상태
   em.detach(member);
```

- **삭제(removed)**

- 삭제된 상태. DB에서도 날린다.

``` java
      // 객체를 삭제한 상태
      em.remove(member);
```

## 영속성 컨텍스트의 이점

- 애플리케이션과 DB사이에 왜 중간에 영속성 컨텍스트가 있냐. 왜 필요하냐. 아래와 같은 개념들이 가능하려면, 영속성 컨텍스트가 존재해야 한다.