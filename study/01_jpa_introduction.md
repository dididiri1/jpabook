# JPA 소개

### JPA 이란?

- Java Persistence API
- 자바 진영의 ORM 기술 표준


### ORM 이란?

- Object-relational mapping(객체 관계 매핑)
- 객체는 객체대로 설계
- 관계형 데이터베이스는 관계형 데이터베이스대로 설계
- ORM 프레임워크가 중간에서 매핑
- 대중적인 언어에는 대부분 ORM 기술이 존재

### JPA는 애플리케이션과 JDBC 사이에서 동작
![](https://github.com/dididiri1/jpabook/blob/main/images/01_01.png?raw=true)

### JPA 동작 - 저장
![](https://github.com/dididiri1/jpabook/blob/main/images/01_02.png?raw=true)

### 동작 - 조회
![](https://github.com/dididiri1/jpabook/blob/main/images/01_03.png?raw=true)


### JPA를 왜 사용해야 하는가?

- SQL 중심적인 개발에서 객체 중심으로 개발
- 생산성
- 유지보수
- 패러다임의 불일치 해결
- 성능
- 데이터 접근 추상화와 벤더 독립성
- 표준

### 생산성 - JPA와 CRUD

- 저장: jpa.persist(member)
- 조회: Member member = jpa.find(memberId)
- 수정: member.setName(“변경할 이름”)
- 삭제: jpa.remove(member)

### JPA와 패러다임의 불일치 해결
![](https://github.com/dididiri1/jpabook/blob/main/images/01_04.png?raw=true)

### JPA의 성능 최적화 기능
- 1차 캐시와 동일성(identity) 보장
- 트랜잭션을 지원하는 쓰기 지연(transactional write-behind)
- 지연 로딩(Lazy Loading)




