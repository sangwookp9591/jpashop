package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    //OrderAPI에 있는 OrderDto를 안쓰는이유 참조해버리면 Repository가 Controller를 참조하는 의존관계가 순환이된다.
    //그리고 findOrderQueryDtos가 만드는 거니깐 OrderQueryDto가 알아야해서 같은 패키지에 넣음


//2
    public List<OrderQueryDto> findOrderQueryDtos() {

        List<OrderQueryDto> result = findOrders();

        //생성자에서 orderItems의의 값을 못채웟기 때문에 하나씩 넣어줘야한다.
        result.forEach(o-> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId()); //쿼리를 각각날림 orderItems를가져와서 orderItems를 넣어줌
            o.setOrderItems(orderItems);
        });
        return result;

    }
//3
    //일대다 이기 때문에 '다' 부분은 따로 해결할 수 없기때문에 쿼리를 다시짜야한다.
    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id,i.name,oi.orderPrice,oi.count) " +
                "from OrderItem oi " +
                "join oi.item i " +
                "where oi.order.id =:orderId", OrderItemQueryDto.class)
                .setParameter("orderId",orderId)
                .getResultList();
    }
//1
    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                 "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id,m.name,o.orderDate,o.status,d.address)" +
                         " from Order o " +
                         "join o.member m " +
                         "join o.delivery d",OrderQueryDto.class)
                 .getResultList();
        //당장 이쿼리를짤땐 , List<OrderItemQueryDto> orderItems 를빼야한다 jpql를 짜더라도 바로 collection을 넣을 수는 없다.
    }
}
