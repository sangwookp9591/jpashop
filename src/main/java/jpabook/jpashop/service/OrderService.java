package jpabook.jpashop.service;

import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepositoryOld;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    private final MemberRepositoryOld memberRepository;

    private final ItemRepository itemRepository;
    /*
    *주문
     */
    @Transactional
    public long order(Long memberId, Long itemId, int count){
        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보
        Delivery delivery =new Delivery();
        delivery.setAddress(member.getAddress());

        //주문상품
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //원래는 delivery save 따로 orderItem save따로 해줘야하는데
        // 여기서는 order만 저장했다 이유는?? orderItems이랑, delivery에 걸려있는 Casecade옵션때문에 그렇다.
        //order를 persist하게되면 order의 orderItems에 들어와있는 애들과 delivery를 같이 persist해주게 된다.

        //근데 문제가 casecade의 범위이다. 어디까지 casecade를 해야할까?
        //명확하진 않지만 개념은 order같은 경우 order가 orderItem을 관리하고, delivery도 관리한다. 이런경우에서만 써야한다.
        //즉 , delivery가 order말고 다른곳에서안쓰고 orderItem도 order말고 다른데서 안쓰는 이런경우에서만 사용해야한다.
        //life cycle에 대해서 동일하게 관리를 할때 의미가 있다.
        //다른데서 참조할 수 없는 private owner인 경우에 도움을 받을 수 있다.
        //다른엔티티에서 참조해서 쓰고 그러면 쓰면 안된다.
        orderRepository.save(order);

        return order.getId();
    }


    //취소
    @Transactional
    public  void cancelOrder(Long orderId){
        //주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        //주문 취소
        order.cancel();
        //평상시같은 경우에는 이런식으로 변경할 경우 변경하는 sql을 또짜야한다.
        //JPA를 활용하면 DATA만 바꾸면 바뀐 변경점을 더티 체킹(변경 감지)으로 찾아서 DB에 UPDATE QUERY를 날림

    }


    //검색
    public List<Order> findOrders(OrderSearch orderSearch){
        return orderRepository.findAllByCriteria(orderSearch);
    }
}
