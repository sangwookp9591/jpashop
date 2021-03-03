package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@DiscriminatorValue("B")//싱글 테이블이라서 저장시 구분을 할수 있어야한다.
public class Book extends Item{

    private String author;
    private String isbn;

}
