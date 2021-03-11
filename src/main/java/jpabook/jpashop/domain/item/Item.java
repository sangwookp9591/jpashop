package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/*
*
* 1. 추상클래스는 뭐? 실체클래스의 공통적인 부분(변수,메서드)를 추출해서 선언한 클래스

2. 추상클래스는 객체를 생성할 수 없다! 아직은 실체성이 없고 구체적이지 않기 때문에!

3. 추상클래스와 실체클래스는 어떤관계? 상속관계!
* */
@BatchSize(size = 1000)
@Entity
@Getter @Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) //한테이블에 다때려박는것
@DiscriminatorColumn(name ="dtype")
public abstract class Item {

    @Id @GeneratedValue
    @Column(name ="item_id")
    private Long id;
    private String name;

    private int price;

    private int stockQuantity;
    //변경할 일이 있으면 setter로 변경하는 것이 아니라 아래와같이 핵심 비지니스 로직으로 변경해야 한다.
    // ->이게 가장 객체지향적인 것이다.

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //===비지니스 로즥===//
    //재고를 줄이고 늘리는 로직
    // 엔티티 자체가 해결할 수 있는 것은 주로 엔티티 안에 비지니스 로직을 넣는게 좋다 -> 이게 객체지향적
    //즉 데이터를 가지고 있는 쪽에 핵심 비지니스 로직이 있는 것이 좋다.

    /*
    * stock 증가
    * */
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    /*
     * stock 감소
     * */
    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;
        System.out.println(" this.stockQuantity = " +  this.stockQuantity);
        System.out.println("quantity = " + quantity);
        System.out.println("restStock = " + restStock);
        if(restStock < 0){
            System.out.println("restStock = " + restStock);
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }

}
