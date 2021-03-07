package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Category {
    @Id
    @GeneratedValue
    @Column(name="category_id")
    private long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "category_item"
            , joinColumns = @JoinColumn(name = "category_id")//중간테이블에 있는 카테고리 ID
            , inverseJoinColumns = @JoinColumn(name="item_id")//이테이블에 아이탬쪽으로 들어가는 컬럼 매핑
    )
    //다대다는 중간테이블 메핑을 위해 조인 테이블이 필요하다.
    //객체는 다 컬렉션 켈랙션이 있어서 다대다가 가능한데,
    //RDB는 컬렉션관계를 양쪽에 가질수 없기때문에 1:N N:1로 풀어내는 중간 테이블이 필요하다.
    //실전에서 쓰지 못하는 경우는 딱 저그림밖에 안됨. 더이상 추가가 안된다.
    private List<Item> items = new ArrayList<>();

    //카테고리 구조가 계층구조로 쭉내려가기때문에 위로도 볼수 있어야하고 아래도 볼수 있어야함.
    //같은 엔티티에 대해 셀프로 양방향 연관관계를 한것이다.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    //연관관계 편의 메소드
    public void addChildCategory(Category child){
        this.child.add(child);
        child.setParent(this);
    }

}
