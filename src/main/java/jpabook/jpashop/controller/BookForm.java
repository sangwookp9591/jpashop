package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class BookForm {
    //공통속성
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    //book만의 속성
    private String author;
    private String isbn;
}
