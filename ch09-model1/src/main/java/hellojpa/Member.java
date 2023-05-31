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
