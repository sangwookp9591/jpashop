package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createFormm(Model model){
        model.addAttribute("form",new BookForm());
        return  "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form){
//        Book book = new Book();
//        //아래처럼 set하는 방식은 좋지 않은 방식이고 createOrder와 같이 생성자 static으로 만드는 방식이 좋다.
//        book.setName(form.getName());
//        book.setPrice(form.getPrice());
//        book.setStockQuantity(form.getStockQuantity());
//        book.setAuthor(form.getAuthor());
//        book.setIsbn(form.getIsbn());

        Book book =createBook(form);

        itemService.saveItem(book);
        return "redirect:/";
    }

    public static Book createBook(BookForm form){
        Book book = new Book();
        //아래처럼 set하는 방식은 좋지 않은 방식이고 createOrder와 같이 생성자 static으로 만드는 방식이 좋다.
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());
        return book;
    }
    @GetMapping("items")
    public String list(Model model){
        List<Item> items =itemService.findItems();
        model.addAttribute("items",items);

        return "items/itemList";
    }

    @GetMapping("items/{itemId}/edit") //itemId 같은 경우는 변경될 수 있으므로 path variable을 써야한다.
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model){
        Book item =(Book) itemService.findOne(itemId);

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(form.getName());
        form.setPrice(form.getPrice());
        form.setStockQuantity(form.getStockQuantity());
        form.setAuthor(form.getAuthor());
        form.setIsbn(form.getIsbn());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    @PostMapping("items/{itemId}/edit") //itemId가 사용자가 임으로 조작할 수있기때문에 서비스단에서 이유저가 아아이탬에 접근해되되는지 권한 로직이 필요하다.
    public String updateItem(@PathVariable("itemId") Long itemId,@ModelAttribute("form") BookForm form){ //@ModelAttribute("form") <form th:object="${form}" method="post">

//        Book book = new Book();
//        book.setId(form.getId());
//        book.setName(form.getName());
//        book.setPrice(form.getPrice());
//        book.setStockQuantity(form.getStockQuantity());
//        book.setAuthor(form.getAuthor());
//        book.setIsbn(form.getIsbn());
//
//        itemService.saveItem(book);
//        return "redirect:items";


        //위에 것보다 더나은 설계
        itemService.updateItem(itemId,form.getName(), form.getPrice(), form.getStockQuantity());
        return "redirect:items";

    }
}
