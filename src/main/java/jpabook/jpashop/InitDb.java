package jpabook.jpashop;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;


/*
*
*
* */

@Component //compoent scan의 대상이 되게함
@RequiredArgsConstructor
public class InitDb { //예제기 때문에 간단히 만듦

    private final InitService initService;

    @PostConstruct// 스프링빈이 다 올라오고나면 PostContruct가 올라옴. 어플리케이션 로딩시점에 호출하고 싶을때
    public void init(){
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService{

        private final EntityManager em;
        public void dbInit1(){
            Member member = createMember("userA", "서울", "1", "1111");
            em.persist(member); //영속상태로 만들어줌

            Book book1 = createBook("JPA1 BOOK", 10000, 100);
            em.persist(book1);

            Book book2 = createBook("JPA2 BOOK", 20000, 100);
            em.persist(book2);

            OrderItem orderItem1 =  OrderItem.createOrderItem(book1,10000,1);
            OrderItem orderItem2 =  OrderItem.createOrderItem(book2,20000,2);

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);//...이기때문에 orderItem 1 2가 들어간것

            em.persist(order);

        }


        public void dbInit2(){
            Member member = createMember("userB", "부산", "2", "2222");
            em.persist(member); //영속상태로 만들어줌

            Book book1 = createBook("SPRING1 BOOK", 20000, 200);
            em.persist(book1);

            Book book2 = createBook("SPRING2 BOOK", 40000, 300);
            em.persist(book2);

            OrderItem orderItem1 =  OrderItem.createOrderItem(book1,20000,3);
            OrderItem orderItem2 =  OrderItem.createOrderItem(book2,40000,4);

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);//...이기때문에 orderItem 1 2가 들어간것

            em.persist(order);

        }

        private Book createBook(String name, int price, int stockQuantity) {
            Book book1 = new Book();
            book1.setName(name);
            book1.setPrice(price);
            book1.setStockQuantity(stockQuantity); //ctrl alt p
            return book1;
        }

        private Member createMember(String name, String city, String street, String zipcode) {
            Member member = new Member();
            member.setName(name);
            member.setAddress(new Address(city, street, zipcode));
            return member;
        }

        private Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }


    }
}


