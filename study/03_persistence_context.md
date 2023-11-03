# 영속성 컨텍스트


## 영속성 컨텍스트

* JPA를 이해하는데 가장 중요한 용어
* "엔티티를 영구 저장하는 환경"이라는 뜻
* **EntityManager.persist(entity);**

![](https://github.com/dididiri1/jpabook/blob/main/images/03_01.png?raw=true)

### 엔티티 매니저? 영속성 컨텍스트?

* 영속성 컨텍스트는 논리적인 개념
* 눈에 보이지 않는다.
* 엔티티 매니저를 통해서 영속성 컨텍스트에 접근

![](https://github.com/dididiri1/jpabook/blob/main/images/03_02.png?raw=true)

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

### 영속성 컨텍스트의 이점

- 1차 캐시
- 동일성(identity) 보장
- 트랜잭션을 지원하는 쓰기 지연(transactional write-behind)
- 변경 감지(Dirty Checking)
- 지연 로딩(Lazy Loading)


### 영속성 조회, 1차 캐시

![](https://github.com/dididiri1/jpabook/blob/main/images/03_03.png?raw=true)

![](https://github.com/dididiri1/jpabook/blob/main/images/03_04.png?raw=true)

![](https://github.com/dididiri1/jpabook/blob/main/images/03_05.png?raw=true)

- 1차 캐시가 있으면 어떤 이점이있을까? 조회할 때 이점이 생긴다.

- find()가 일어나는 순간, 엔티티 매니저 내부의 1차 캐시를 먼저 찾는다.

- 1차 캐시에 엔티티가 존재하면 바로 반환한다. DB 들리지 않는다.

- 그렇케 큰 이점은 없음.

### 동일성(identity) 보장

- 영속 엔티티의 동일성을 보장한다.

- 1차 캐시로 반복 가능한 읽기(REPEATABLE READ) 등급의 트랜잭션 격리 수준을 **데이터베이스가 아닌 애플리케이션 차원에서 제공**한다.

``` java
   // 동일성 
   Member findMember1 = em.find(Member.class, 101L);
   Member findMember2 = em.find(Member.class, 101L);

   System.out.println("result =" + (findMember1 == findMember2)); 
```

```sql
Hibernate: 
    select
        member0_.id as id1_0_0_,
        member0_.name as name2_0_0_ 
    from
        Member member0_ 
    where
        member0_.id=?
Hibernate: 
    select
        member0_.id as id1_0_0_,
        member0_.name as name2_0_0_ 
    from
        Member member0_ 
    where
        member0_.id=?
        
result = true

``` 

### 엔티티 등록 - 트랜잭션을 지원하는 쓰기 지연

- tx.commit() 하게 되면, flush() 와 commit() 가 일어난다.

``` java
  EntityManager em = emf.createEntityManager();
  EntityTransaction transaction = em.getTransaction();
  // 엔티티 매니저는 데이터 변경시 트랜잭션을 시작해야 한다.
  transaction.begin(); // 트랜잭션 시작
  
  em.persist(memberA);
  em.persist(memberB);
  // 이때까지 INSERT SQL을 데이터베이스에 보내지 않는다.
  
  // 커밋하는 순간 데이터베이스에 INSERT SQL을 보낸다.
  transaction.commit(); // 트랜잭션 커밋
``` 

- 사이즈 만큼 모아서 한방에 처리하거 쿼리를 보내고 commit 됨

``` java
  <property name="hibernate.jdbc.batch_size" value=10/>
``` 

### 엔티티 수정 - 변경감지(Dirty Checking)

``` java
  EntityManager em = emf.createEntityManager();
  EntityTransaction transaction = em.getTransaction();
  transaction.begin(); // 트랜잭션 시작
  
  // 영속 엔티티 조회
  Member memberA = em.find(Member.class, "memberA");
  
  // 영속 엔티티 수정
  memberA.setUsername("nj");
  memberA.setAge(27);
  
  //em.update(member) 또는 em.persist(member)로 다시 저장해야 하지 않을까?
  
  transaction.commit(); // 트랜잭션 커밋
``` 

- update할때는 persist() 할 필요 없음 
- commit()또는 flush()가 일어날 때 1차 캐시에 있는 엔티티와 스냅샷을 다 비교함, 비교해서 바뀌면 데이터에 반영하고 commit 함

![](https://github.com/dididiri1/jpabook/blob/main/images/03_06.png?raw=true)

``` java
  Member memberA = em.find(Member.class, "memberA");

  em.remove(memberA); // 엔티티 삭제
``` 

### Reference

- [자바 ORM 표준 JPA 프로그래밍](https://www.inflearn.com/course/ORM-JPA-Basic)
