package hellojpa;

import lombok.Setter;
import javax.persistence.*;

@Entity
@Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    // 기간 Period
    @Embedded // 값 타입을 사용하는 곳은 Embedded
    private Period workPeriod;

    // 집 주소 Address
    @Embedded
    private Address homeAddress;

}
