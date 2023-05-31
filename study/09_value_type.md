# 값 타입

> * 기본값 타입
> * **임베디드 타입(복합 값 타입)**
> * 값 타입과 불변 객체
> * 값 타입의 비교
> * **값 타입 컬렉션**


## JPA의 데이터 타입 분류

### 엔티티 타입

- @Entity로 정의하는 객체
* 데이터가 변해도 식별자로 지속해서 추적 가능
* 예) 회원 엔티티의 키나 나이 값을 변경해도 식별자로 인식 가능


### 값 타입

* int, Interger, String처럼 단순히 값으로 사용하는 자바 기본 타입이나 객체
* 식별자가 없고 값만 있으므로 변경시 추적 불가
* 예) 숫자 100을 200으로 변경하면 완전히 다른 값으로 대체

## 값 타입 분류

* **기본값 타입**
  * 자바 기본 타입(int, double)
  * 래퍼 클래스(Integer, Long)
  * String

* **임베디드 타입**(embedded type, 복합 값 타입)

* **컬렉션 값 타입**(collection value type)

### 기본값 타입

* 예): String name, int age
* 생명주기를 엔티티의 의존
  * 예) 회원을 삭제하면 이름, 나이 필드도 함께 삭제

* 값 타입은 공유하면X
  * 예) 회원 이름 변경시 다른 회원이 이름도 함께 변경되면 안됨

### 참괴 자바의 기본 타입은 절대 공유 X

* int, double 같은 기본 타입(Primitive type)은 절대 공유 X
* 기본 타입은 항상 값을 복사함
* Integer같은 래퍼 클래스나 String 같은 특수한 클래스는 공유 가능한 객체지만 변경 X


  ```java
  public class ValueMain {

    public static void main(String[] args) {

        int a = 10;
        int b = a;

        a = 20 ;

        System.out.println("a = " + a);
        System.out.println("b = " + b);
    }
  }
  ```

## 임베이드 타입

* 새로운 값 타입을 직접 정의할 수 있음
* JPA는 임베이드 타입(embedded type)이라 함
* 주로 기본 값 타입을 모아서 만들어서 복합 값 타입이라고도 함
* int, String과 같은 값 타입

* 회원 엔티티는 이름, 근무 시작일, 근무 종료일, 주소 도시, 주서 번지, 주소 우편번호를 가진다.

## 사용하는 이유

* 실제로는 회원은 이름, 근무기간, 집주소 가지고 있어요 라고 추상화해서 밑에 그림 처럼 쉽게 표현한다.

* 공통적으로 객체화 할때

![](https://github.com/dididiri1/jpabook/blob/main/images/09_01.png?raw=true)


![](https://github.com/dididiri1/jpabook/blob/main/images/09_02.png?raw=true)

* @Embeddable: 값 타입을 정의하는 곳에 표시
* @Embedded: 값 타입을 사용한 곳에 표시
* 기본 생성자 필수 (Lombok 사용하면 될듯?)

## 임베이드 타입의 장점

* 재사용
* 높은 응집도
* Period.isWork()처럼 해당 값 타입만 사용하는 의마 있는 메소드를 만들 수 있음
* 임베디드 타입을 포함한 모든 값 타입은, 값 타입을 소유한 엔티티에 생명주기를 의존함

## 임베디드 타입과 테이블 매핑
* 임베디드 타입은 엔티티의 값일 뿐이다!.
* 임베디드 타입을 사용하기 전과 후 매핑하는 테이블은 같다.
* 객체와 테이블을 아주 세밀하게(find-grained) 매핑하는 것이 가능
* 잘 설계한 ORM 애플리케이션은 매핑한 테이블 수보다 클래스의 수가 더 많음

## @AttributeOverride: 속성 재정의

* 한 엔티티에서 같은 값 타입을 사용할때 씀
* 컬럼 명이 중복됨
* @AttributeOverrides, @AttributeOverride를 사용해서 컬러 명 속성을 재정의

```java
  @Entity
  public class Member {
  
    // 집 주소 Address
    @Embedded
    private Address homeAddress;

    // 회사 주소 Address
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="city",
                    column=@Column(name="WORK_CITY")),
            @AttributeOverride(name="street",
                    column=@Column(name="WORK_STREET")),
            @AttributeOverride(name="zipcode",
                    column=@Column(name="WORK_ZIPCODE"))
    })
    private Address workAddress;
    
  }
```

### 임베디드 타입과 null

* 임베디드 타입의 값이 null이라면, 매핑한 컬럼 값은 모두 null로 저장 된다.


## 값 타입과 불변 객체

### 값 타입 공유 참조

* 임베디드 타입 값은 값 타입을 여러 엔티티에서 공유하면 위험함
* 부작용(side effect) 발생

![](https://github.com/dididiri1/jpabook/blob/main/images/09_03.png?raw=true)

### 값 타입 복사

* 값 타입의 실제 인스턴스인 값을 공유하는 것은 위험
* 대신 값(인스턴스)를 복사해서 사용


![](https://github.com/dididiri1/jpabook/blob/main/images/09_04.png?raw=true)



``` java
   
   Address address = new Address("city", "street", "10000");

   Member member1 = new Member();
   member1.setUsername("member1");
   member1.setHomeAddress(address);

   em.persist(member1);

   // 값 복사해서 새로운 객체 생성
   Address copyAddress = new Address(address.getCity(), address.getStreet(), address.getZipcode());

   Member member2 = new Member();
   member2.setUsername("member2");
   member2.setHomeAddress(copyAddress);

   em.persist(member2);

   member1.getHomeAddress().setCity("newCity");  
    
```



## 참고사항
* 같은 객체 사용하면 공유되서 수정하면 같이 수정되는 버그가 있음
* 만약에 공유해서 사용할 일 있으면 새로 객체생성해서 복사해서 사용하기.
* 근데 다른사람이 까먹고 복사 안하고 쓰면 망함.

## 객체 타입의 한계
* 항상 값을 복사해서 사용하면 공유 참조로 인해 발생하는 부작용을 피할 수 있다.
* 문제는 임베디드 타입처럼 **직접 정의한 값 타입은 자바의 기본 타입이 아니라 객체 타입**이다.
* **객체 타입은 참조 값을 직접 대입하는 것을 막을 방법이 없다.**
* **객체의 공유 참조는 피할 수 없다.**

## 객체 타입의 한계

``` java
public class ValueMain {

  public static void main(String[] args) {

    // 기본 타입(primitive type)
    int a = 10;
    int b = a; // 기본 타입은 값을 복사
    b = 4;

    System.out.println("a = " + a);
    System.out.println("b = " + b);


    // 객체 타입
    Address addressA = new Address("city", "street", "zipcode");
    Address addressB = addressA; // 객체 타입은 참조를 전달
    addressB.setCity("newCity");

    // 참조를 넘겨서 수정하면 값이 영향이 있음
    System.out.println("addressA = " + addressA.getCity());
    System.out.println("addressB = " + addressB.getCity());
  }
}  

```

``` java

  a = 10
  b = 4
          
  addressA = newCity
  addressB = newCity

```

## 불변 객체

* 객체 타입을 수정할 수 없게 만들면 **부작용을 원천 차단**
* **값 타입은 불변 객체(immutable object)로 설계해야함**
* **불벼 객체: 생성 시점 이후 절대 값을 변경할 수 없는 객체**
* 생성자로만 값을 설정하고 수정자(@Setter)를 만들지 않으면 됨
* 참고: Integer, String은 자바가 제공하는 대표적인 불변 객체
