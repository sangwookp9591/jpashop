package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/*
*
* 1. 추상클래스는 뭐? 실체클래스의 공통적인 부분(변수,메서드)를 추출해서 선언한 클래스

2. 추상클래스는 객체를 생성할 수 없다! 아직은 실체성이 없고 구체적이지 않기 때문에!

3. 추상클래스와 실체클래스는 어떤관계? 상속관계!
* */
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

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

}
