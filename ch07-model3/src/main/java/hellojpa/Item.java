package hellojpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // SINGLE_TABLE : 단일 테이블 전략
@DiscriminatorColumn // 엔티티명으로 구분값 해줌 디폴트 = dtype (관행으로 그냥씀)
public abstract class Item extends BaseEntity{

    @Id @GeneratedValue
    private Long id;

    private String name;

    private int price;

    private int stockQuantity;


}
