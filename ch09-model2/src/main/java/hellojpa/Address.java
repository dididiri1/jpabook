package hellojpa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable // 값 타입을 정의한곳은 Embeddalbe
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private String city;

    private String street;

    private String zipcode;
}
