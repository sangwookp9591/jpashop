package jpabook.jpashop.service;

import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.domain.item.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ItemServiceTest {

    @Autowired private ItemService itemService;
    @Autowired private ItemRepository itemRepository;

    @Test
    public void 상품등록() throws Exception{
        //given
        Book book = new Book();
        book.setName("박");
        book.setAuthor("작가1");

        //when
        itemService.saveItem(book);

        //then
        assertEquals(book,itemRepository.findOne(book.getId()));
    }
}