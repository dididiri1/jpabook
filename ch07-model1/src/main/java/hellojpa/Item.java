package hellojpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter

@Inheritance(strategy = InheritanceType.JOINED) // JOINED : 조인 전략
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // SINGLE_TABLE : 단일 테이블 전략ㄹㄹ
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS) // TABLE_PER_CLASS : 구현 클래스마다 테이블 전략 DiscriminatorColumn 해도 안됨 얘는

// @DiscriminatorColumn(name = "DIS_TYPE") 이름 정할수도 있음
@DiscriminatorColumn // 엔티티명으로 구분값 해줌 디폴트 = dtype (관행으로 그냥씀)
public abstract class Item {

    @Id @GeneratedValue
    private Long id;

    private String name;

    private int price;
}
