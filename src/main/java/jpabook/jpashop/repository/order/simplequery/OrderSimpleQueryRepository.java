package jpabook.jpashop.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id,m.name,o.orderDate,o.status,d.address)" + //address는 valye 타입이라서 가능
                        " from Order o" +
                        " join o.member m " +
                        "join o.delivery d",OrderSimpleQueryDto.class)
                .getResultList();

        //여기서 보면 o가 select되는데 OrderSimpleQueryDto에 매핑이 될수가 없다. 생성자에 들어가서 매핑되고 그러는게 아니기 때문에
        //JPA는 entity나 value object(embedable)만 반환 할 수 있다. Dto같은것은 안되고 new operation을 꼭써야한다.

    }

}
