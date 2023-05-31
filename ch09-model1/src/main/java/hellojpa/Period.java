package hellojpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable // 값 타입을 정의한곳은 Embeddalbe
@Getter @Setter
public class Period {

    private LocalDateTime startDate;
    private LocalDateTime endDate;



}
