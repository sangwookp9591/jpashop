package jpabook.jpashop.service.query;

import jpabook.jpashop.api.OrderApiController;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderQueryService {

    private final OrderRepository orderRepository;

//    public List<OrderDto> orderV3(){
//        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset,limit);
//        List<OrderApiController.OrderDto> result = orders.stream()
//                .map(o -> new OrderApiController.OrderDto(o))
//                .collect(toList());
//        return result;
//    }

}
