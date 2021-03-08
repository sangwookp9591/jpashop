package jpabook.jpashop.api;

import jpabook.jpashop.Repository.OrderRepository;
import jpabook.jpashop.Repository.OrderSearch;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

}
