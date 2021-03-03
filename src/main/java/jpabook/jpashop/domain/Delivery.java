package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Delivery {
    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery",fetch = FetchType.LAZY)//1:1일경우 왜래키를 어디에 두냐? access 를 많이하는곳을 기본으로 둔다.
    private Order order;

    @Embedded
    private Address address;

    //enum 타입은 @Enumerated을꼭 넣어야하고 EnumType String을 꼭해줘야한다.
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;
}
