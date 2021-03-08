package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;
import org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //em.find 해서 한건 들고올때는 가능
    @JoinColumn(name = "member_id")//외래키 이름이 member_id가 된다.
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    //casecade orderitems에다가 데이터를 넣어두고 저장하면 order까지 같이 저장된다.
    private List<OrderItem> orderItems = new ArrayList<>();
    /*
    * persist(orderItemA)
    * persist(orderItemB)
    * persist(orderItemC)
    * persist(order)를해줘야하는데
    *
    * casecade는
    * presist(order)만 해줘도된다.
    *
    * order를 persist하면 casecade는 전파하여 orderItems안에 있는 A,B,C persist같이해준다 delete할때도 같이 지워버린다.
    * */

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)//1:1일경우 왜래키를 어디에 두냐? access 를 많이하는곳을 기본으로 둔다.
    //연관관게의 주인을 order의 delivery로 한다.
    //order를 저장할떄 delivery도 같이 저장해준다.
    //모든 엔티티는 persist를 각각해줘야하는데 casecade를 해주면 같이 persist호출된다.
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;


    private LocalDateTime orderDate; //주문시간
    //Date를 쓸경우 오노테이션을 따로 매핑해줘야하지만
    // localDateTime은 하이버네이트가 지원을 해준다.

    @Enumerated(EnumType.STRING)
    private OrderStatus status;//주문상태 [ORDER,CANCEL]

    //==연관관계 편의 메서드==//
    //만드는 이유
    //양방향 연관관계를 세팅할려고하면 값을 양쪽에 다 넣어줘야한다.
    //DB에 저장하는건 연관관계 주인에 있으면 되는데 로직을 태울떄 왓다갓다할려면 양쪽에 다 필요하다.
    //원래대로라고하면
    /*
    * Member member =  new Member();
    * Order order = new Order();
    *
    * member.getOrders().add(order);
    * order.setMember(member); 이렇게 되어야 한다.
    *
    * 문제는 코드를 작성하다보면 사람들이 깜빡할 수 있기 떄문에
    * 두개를 원자적으로 묶는 메서드를 만드는 것이다.
    * */
    //편의 메서드는 어디있으면 좋은가? 실직적으로 컨트롤 하는 곳에 있으면 좋다.
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //====생성 메서드====//

    // 밖에서 set하는 방식이아니라 생성할때부터 값을 넣어 생성 메서드에서 주문에대한 복잡한 비지니스 로직을 완결시킴
    public static Order createOrder(Member member,Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems){
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());

        return order;
    }



    //===비지니스 로직 ====/

    /*
    주문 취소
    *
     */
    public void cancel(){
        if(delivery.getStatus() == DeliveryStatus.COMP){ //상태가 배송완료이면 주문을 못하다.
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL); //validation 통과하면 order의 상태를 cancel로 변경

        //this.orderItems 으로 써야하지만 여기선 색깔을 표시해주기도하고 this는 이름이 똑같을 때랑 강조할때만 쓴다.
        for(OrderItem orderItem : orderItems){//루프를 돌면서 orderItem에대해 cancel을 하면
            orderItem.cancel(); //재고 증가
        }
    }




    //====조회 로직 ===// 계산이 필요할 경우

    /*
    * 전체 주문 가격 조회
    * */
    public int getTotalPrice(){
//      int totalPrice = 0;
//        for (OrderItem orderItem : orderItems){
//            totalPrice += orderItem.getTotalPrice(); //가격 * 수량
//        }
        //람다식으로 변환 alt+enter
        int totalPrice = orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
        return totalPrice;
    }

}
