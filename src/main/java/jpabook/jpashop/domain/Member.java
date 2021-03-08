package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue //자동생성
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded//임베더블이나 임베디드 둘중하나만있어도 되지만 둘다써준다.
    private Address address;
    //order table 에 있는 member field에 의해서 메핑된거야 읽기전용
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

}
