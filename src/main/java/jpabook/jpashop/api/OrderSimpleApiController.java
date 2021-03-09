package jpabook.jpashop.api;

import jpabook.jpashop.Repository.OrderRepository;
import jpabook.jpashop.Repository.OrderSearch;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/*
* xToOne -> (ManyToOne, OneToOne)단계에서 문제를 어떻게 최적회하는지
* Order
* Order -> Member
* Order -> Delivery
* */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;


    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : all) {
            order.getMember() //order.getMember() 프록시 객체
                    .getName(); //.getName()때 실제 name을 끌고와야하기 때문에 DB에서 MEMBER 호출 Lazy 강제 초기화
            order.getDelivery().getAddress(); //위랑 같음
        }
        return all;
    }
    /**
     * V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용X)
     * - 단점: 지연로딩으로 쿼리 N번 호출
     */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
//        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
//        List<SimpleOrderDto> collect = orders.stream() //주문을 심플오더디티오로 변경
//                .map(o -> new SimpleOrderDto(o))// .map은 a를 b로 바꾸는 것이다.
//                .collect(Collectors.toList()); //얘를 collect로 해서 list로 변환
//        return collect;

//        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
//        return orders.stream() //주문을 심플오더디티오로 변경
//                .map(o -> new SimpleOrderDto(o))
//                .collect(Collectors.toList());
        
//        return orderRepository.findAllByCriteria(new OrderSearch()).stream() //주문을 심플오더디티오로 변경
//                .map(o -> new SimpleOrderDto(o))
//                .collect(Collectors.toList());
        
        return orderRepository.findAllByCriteria(new OrderSearch()).stream() //주문을 심플오더디티오로 변경
                .map(SimpleOrderDto::new)
                .collect(Collectors.toList());

    }
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    @Data
    static class SimpleOrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order){ //DTO가 엔티티를 가지는것은 문제가 되지 않는다.
            orderId = order.getId();
            name =order.getMember()
                    .getName(); //lazy  초기화
            orderDate =order.getOrderDate();
            orderStatus =order.getStatus();
            address= order.getDelivery()
                    .getAddress();//lazy  초기화
        }

    }
}
