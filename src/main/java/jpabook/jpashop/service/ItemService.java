package jpabook.jpashop.service;

import jpabook.jpashop.Repository.ItemRepository;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }


    @Transactional
    public void updateItem(Long itemId ,Book bookParam){//itemParam: 파리미터로 넘어온 준영속 상태의 엔티티
        //1. 변경감지지
       //이렇게 해야한다 이게 더나은 방법이다.

        //아이탬 기반으로 실제 DB에 있는 영속상태 ENTITY를 찾아옴.
        //같은 엔티티를 조회한다.
        Item findItem = itemRepository.findOne(itemId);

        //데이터를 수정한다.
        //trascntional에 의해서 커밋이되고 flush가 나간다.
        findItem.setPrice(bookParam.getPrice());
        findItem.setName(bookParam.getName());
        findItem.setStockQuantity(bookParam.getStockQuantity());
    }

    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }

}
