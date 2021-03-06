package jpabook.jpashop.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.QMember;
import jpabook.jpashop.domain.QOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static jpabook.jpashop.domain.QMember.member;
import static jpabook.jpashop.domain.QOrder.*;

@Repository
public class OrderRepository {

    private final EntityManager em;
    private final JPAQueryFactory query ;

    public OrderRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(long orderId){
        return em.find(Order.class, orderId);
    }

    public List<Order> findAllOld(OrderSearch orderSearch){
//        return em.createQuery("select o from Order o join o.member m" +
//                        "where o.status = :status" +
//                        "and m.name like :name"
//                , Order.class)
//                .setParameter("status",orderSearch.getOrderStatus())
//                .setParameter("name",orderSearch.getMemberName())
//                .getResultList();
        //여기서 문제점이 값이 없으면 setParameter자체가 필요없어진다 .이렇듯 요구에따라 동적 쿼리가 되야한다.
        //아니면 name이 null이면 주문이던 취소던 상태 체크하지말고 다들고와! 이런건 어떻게하면 될까?

        //1. pql을 문자열로 쌩자로 하는것이다. 이렇게쓰면안됨.! 문자열을 더하는거기때문에 버그 발생확률 높다
        String jpql = "select o from Order o join o.member m";

        boolean isFirstCondition = true;
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) { //뭔가 값이 있으면
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }


        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {//text에 이값이 있으면 어떻게 합니까?
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();



    }
    /*
    *
    * JPA Criteria
    * */
    //JPA제공하는 동적쿼리를 만들기위한 표준이다. 이것도 권장하는 방법은 아니다. why? 실무에서 쓰라고 만든게 아니다.
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" +
                            orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대1000건

        return query.getResultList();


    }

    public List<Order> findAll(OrderSearch orderSearch){

        return query.select(order)
                .from(order)
                .join(order.member, member) //order과 member를 조인할떄 member alias
                .where(statusEq(orderSearch.getOrderStatus()), nameLike(orderSearch.getMemberName())) // ,는 and 조건
                .limit(100)
                .fetch();

    }



    private BooleanExpression statusEq(OrderStatus statusCond){
        if (statusCond == null){
            return null;
        }
        return order.status.eq(statusCond);
    }
    private BooleanExpression nameLike(String memberName) {
        if (StringUtils.hasText(memberName)) {
            return null;
        }
        return member.name.like(memberName);
    }



    public List<Order> findAllWithMemberDelivery() {
        //select절에서 다가져옴 한방쿼리로 order member delivery를 다들고옴.
        //lazy proxy다 무시하고 진짜 값 다채워서 가져온다
        return em.createQuery("select o from Order  o" +
                        " join fetch o.member" +
                        " join fetch o.delivery d"
                , Order.class)
                .getResultList();
    }

        public List<Order> findAllWithMemberDelivery(int offset, int limit) {
            return em.createQuery(
                    "select o from Order o" +
                            " join fetch o.member m" +
                            " join fetch o.delivery d", Order.class)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .getResultList();
        }



    public List<Order> findAllWithItem() {
        return em.createQuery("select distinct o from Order o " +
                "join fetch o.member m " +
                "join fetch o.delivery d " +
                "join fetch o.orderItems oi " +
                "join fetch oi.item i", Order.class).getResultList();
    }
    //distinct를 걸어주면 DB distinct이외에 한가지를 더해준다.
    //order의 id가 중복된 것이있으면 중복을 제거해준다.
    // }


}
