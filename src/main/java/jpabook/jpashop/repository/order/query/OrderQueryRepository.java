package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    //OrderAPI에 있는 OrderDto를 안쓰는이유 참조해버리면 Repository가 Controller를 참조하는 의존관계가 순환이된다.
    //그리고 findOrderQueryDtos가 만드는 거니깐 OrderQueryDto가 알아야해서 같은 패키지에 넣음


    //V4
//2
    public List<OrderQueryDto> findOrderQueryDtos() {

        List<OrderQueryDto> result = findOrders();

        //생성자에서 orderItems의의 값을 못채웟기 때문에 하나씩 넣어줘야한다.
        result.forEach(o-> {
            List<OrderItemQueryDto> orderItems = findOrderItemMap(o.getOrderId()); //쿼리를 각각날림 orderItems를가져와서 orderItems를 넣어줌
            o.setOrderItems(orderItems);
        });
        return result;

    }

//3 //V4
    //일대다 이기 때문에 '다' 부분은 따로 해결할 수 없기때문에 쿼리를 다시짜야한다.
    private List<OrderItemQueryDto> findOrderItemMap(Long orderId) {
        return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id,i.name,oi.orderPrice,oi.count) " +
                "from OrderItem oi " +
                "join oi.item i " +
                "where oi.order.id =:orderId", OrderItemQueryDto.class)
                .setParameter("orderId",orderId)
                .getResultList();
    }

//1 //V4
    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                 "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id,m.name,o.orderDate,o.status,d.address)" +
                         " from Order o " +
                         "join o.member m " +
                         "join o.delivery d",OrderQueryDto.class)
                 .getResultList();
        //당장 이쿼리를짤땐 , List<OrderItemQueryDto> orderItems 를빼야한다 jpql를 짜더라도 바로 collection을 넣을 수는 없다.
    }

    //V5
    //리펙토링 후
    public List<OrderQueryDto> findAllByDto_optimization() {
        //Query 발생
        List<OrderQueryDto> result = findOrders();//이전꺼 단점이 루프를 도는데 한방에 가져올것이다.

        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(toOrderIds(result));

        //앞에꺼는 loop를 돌릴때마다 쿼리를 날렷는데 이것은 메모리에 맵으로 가져온다음에 메모리에서 매칭을 해서 값을 가져옴.
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }
    //V5
    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        //Query 발생
        //여기서 뽑은 orderIds를 파라미터 인절에 바로 넣는다.
        List<OrderItemQueryDto> orderItems = em.createQuery("" +
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id,i.name,oi.orderPrice,oi.count) " +
                "from OrderItem oi " +
                "join oi.item i " +
                "where oi.order.id in :orderIds", OrderItemQueryDto.class)//where oi.order.id =:orderId"   기존V4에서 아이디를 하나씩가져오는것을 IN절로 한번에 가져옴.
                .setParameter("orderIds", orderIds)
                .getResultList();


        //위에 orderItems를 바로 써도되지만 최적화 해줌 map으로 코드도 작성하기쉽고 성능도 최적화 하기 위해서
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()//group by를 통해 map으로 바꿀수 있다.
                .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));
        //orderItemQueryDto.getOrderId()를 기준으로해서 map으로 바꿀수가 있다.
        return orderItemMap;
    }
    //V5
    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        List<Long> orderIds = result.stream()
                .map(o -> o.getOrderId()) //map으로 orderQueryDto를 orderId로 변환
                .collect(Collectors.toList());
        return orderIds;
    }
    //리펙토링전
//    public List<OrderQueryDto> findAllByDto_optimization() {
//        //Query 발생
//        List<OrderQueryDto> result = findOrders();//이전꺼 단점이 루프를 도는데 한방에 가져올것이다.
//
//        List<Long> orderIds = result.stream()
//                .map(o -> o.getOrderId()) //map으로 orderQueryDto를 orderId로 변환
//                .collect(Collectors.toList());
//
//        //Query 발생
//        //여기서 뽑은 orderIds를 파라미터 인절에 바로 넣는다.
//        List<OrderItemQueryDto> orderItems = em.createQuery("" +
//                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id,i.name,oi.orderPrice,oi.count) " +
//                "from OrderItem oi " +
//                "join oi.item i " +
//                "where oi.order.id in :orderIds", OrderItemQueryDto.class)//where oi.order.id =:orderId"   기존V4에서 아이디를 하나씩가져오는것을 IN절로 한번에 가져옴.
//                .setParameter("orderIds", orderIds)
//                .getResultList();
//
//
//        //위에 orderItems를 바로 써도되지만 최적화 해줌 map으로 코드도 작성하기쉽고 성능도 최적화 하기 위해서
//        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()//group by를 통해 map으로 바꿀수 있다.
//                .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));
//        //orderItemQueryDto.getOrderId()를 기준으로해서 map으로 바꿀수가 있다.
//
//
//        //앞에꺼는 loop를 돌릴때마다 쿼리를 날렷는데 이것은 메모리에 맵으로 가져온다음에 메모리에서 매칭을 해서 값을 가져옴.
//        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));
//
//        return result;
//    }


    //v6

    //이쿼리를 그대로실해아하면 중복을 포함해서 나갈수 밖에 없다.
    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderFlatDto(" +
                        "o.id,m.name,o.orderDate,o.status,d.address,i.name,oi.orderPrice,oi.count) " +
                        "from Order o " +
                        "join o.member m " +
                        "join o.delivery d " +
                        "join o.orderItems oi " +
                        "join oi.item i",
        OrderFlatDto.class)
                .getResultList();
    }

}
