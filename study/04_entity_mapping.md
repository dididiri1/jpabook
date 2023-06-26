# 엔티티 매핑

### 엔티티 매핑 소개
- 객체와 테이블 매핑: @Entity, @Table
- 필드와 컬럼 매핑: @Column
- 기본 키 매핑: @Id
- 연관관계 매핑: @ManyToOne,@JoinColumn

## 객체와 테이블 매핑

### @Entity

- @Entity가 붙은 클래스는 JPA가 관리, 엔티티라 한다.
- JPA를 사용해서 테이블과 매핑할 클래스는 **@Entity** 필수
- 주의
  - **기본 생성자 필수**(파라미터가 없는 public 또는 protected 생성자)
  - final 클래스, enum, interface, inner 클래스 사용X
  - 저장할 필드에 final 사용 X

### @Entity 속성 정리

- @Entity(name = "Member")
  - JPA에서 사용할 엔티티 이름을 지정한다.
  - 기본값: 클래스 이름을 그대로 사용(예: Member)
  - 같은 클래스 이름이 없으면 가급적 기본값을 사용한다.

### @Table

- @Table은 엔티티와 매핑할 테이블 지정

| 속성                   | 기능                               | 기본값      |
| ---------------------- | ---------------------------------- | ----------- |
| name                   | 매핑할 테이블 이름                 | 엔티티 이름 |
| catalog                | 데이터베이스 catalog 매핑          |             |
| schema                 | 데이터베이스 schema 매핑           |             |
| uniqueConstraints(DDL) | DDL 생성시에 유니크 제약 조건 생성 |             |

### 데이터베이스 스키마 자동 생성

- DDL을 애플리케이션 실행 시점에 자동 생성
- 테이블 중심 -> 객체 중심
- 데이터베이스 방언을 활용해서 데이터베이스에 맞는 적절한 DDL 생성
- 이렇게 생성된 DDL은 개발 장비에서만 사용
- 생성된 DDL은 운영서버에서는 사용하지 않거나, 적절히 다듬은 후 사용

### 데이터베이스 스키마 자동 생성 - 속성
- hibernate.hbm2ddl.auto

  | 옵션        | 설명                               |
  | ----------- |----------------------------------|
  | create      | 기존 테이블 삭제 후 다시 생성(DROP + CREATE) |
  | create-drop | create와 같으나 종료시점에 테이블 DROP       |
  | update      | 변경분만 반영(운영DB에는 사용하면 안됨).         |
  | validate    | 엔티티와 테이블이 정상 매핑되었는지만 확인          |
  | none        | 사용하지 않음                          |

- 데이터베이스 스키마 자동 생성 - 주의
- **운영 장비에는 절대 create, create-drop, update 사용하면 안된다.**
- 개발 초기 단계는 create 또는 update
- 테스트 서버는 update 또는 validate
- 스테이징과 운영 서버는 validate 또는 none

### DDL 생성 기능

- 제약조건 추가: 회원 이름은 **필수**, 10자 초과X
  - **@Column(nullable = false, length = 10)**
- 유니크 제약조건 추가
``` java
@Table(uniqueConstraints = {@UniqueConstraint( name = "NAME_AGE_UNIQUE",
                                             columnNames = {"NAME", "AGE"} )})
```
- DDL 생성 기능은 DDL을 자동 생성할 때만 사용되고 JPA의 실행 로직에는 영향을 주지 않는다.

### 필드와 컬럼 매핑

- 요구사항
  - 회원은 일반 회원과 관리자로 구분해야 한다. 
  - 회원 가입일과 수정일이 있어야 한다.
  - 회원을 설명할 수 있는 필드가 있어야 한다. 이 필드는 길이 제한이 없다.

```java
@Entity
public class Member {

    @Id
    private Long id;

    @Column(name = "name")
    private String username;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    @Lob
    private String description;
    
    //Getter, Setter...
}
```

### 매핑 어노테이션 정리
-hibernate.hbm2ddl.auto

| 어노테이션        | 설명                              |
| ----------- |---------------------------------|
| @Column      | 컬럼 매핑 |
| @Temporal  | 날짜 타입 매핑       |
| @Enumerated      | enum 타입 매핑         |
| @Lob    | BLOB, CLOB 매핑          |
| @Transient        | 특정 필드를 컬럼에 매핑하지 않음(매핑 무시)|

### @Lob

**데이터베이스 BLOB, CLOB 타입과 매핑**
- @Lob에는 지정할 수 있는 속성이 없다.
- 매핑하는 필드 타입이 문자면 CLOB 매핑, 나머지는 BLOB 매핑
  - CLOB: String, char[], java.sql.CLOB
  - BLOB: byte[], java.sql. BLOB

### @Transient
- 필드 매핑X
- 데이터베이스에 저장X, 조회X
- 주로 메모리상에서만 임시로 어떤 값을 보관하고 싶을 때 사용

## 기본 키 매핑

### 기본 키 매핑 어노테이션

- @Id 
- @GeneratedValue

``` java
@Id @GeneratedValue
private Long id;
```

### 기본 키 매핑 방법

- 직접 할당: @Id만 사용
- 자동 생성(@GeneratedValue)
  - IDENTITY: 데이터베이스에 위임, MYSQL
  - SEQUENCE: 데이터베이스 시퀀스 오브젝트 사용, ORACLE 
    - @SequenceGenerator 필요
  - TABLE: 키 생성용 테이블 사용, 모든 DB에서 사용
    - @TableGenerator 필요
  - AUTO: 방언에 따라 자동 지정, 기본값


### TABLE 전략

- 키 생성 전용 테이블을 하나 만들어서 데이터베이스 시퀀스를 흉내내는 전략
- 장점: 모든 데이터베이스에 적용 가능
- 단점: 성능

### 권장하는 식별자 전략

- 기본 키 제약 조건: null 아님, 유일, 변하면 안된다.
- 미래까지 이 조건을 만족하는 자연키는 찾기 어렵다. 대리키(대체키)를 사용하자.
- 예를 들어 주민등록번호도 기본 키로 적절하기 않다.
- 권장: Long형 + 대체키 + 키 생성전략 사용

### 데이터 중심 설계의 문제점
- 현재 방식은 객체 설계를 테이블 설계에 맞춘 방식
- 테이블의 외래키를 객체에 그대로 가져옴
- 객체 그래프 탐색이 불가능
- 참조가 없으므로 UML도 잘못됨
























