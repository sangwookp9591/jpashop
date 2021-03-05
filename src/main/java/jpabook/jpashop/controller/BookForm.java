package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class BookForm {
    private Long id;   //공통속성
    private String name;
    private int price;
    private int stockQuantity;
    private String author;  //book만의 속성
    private String isbn;
}
