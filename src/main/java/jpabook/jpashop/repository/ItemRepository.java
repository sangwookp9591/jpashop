package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item){
        if(item.getId() == null){ //아이탬은 처음에 아이디가 없다(데이터 저장하는 시점까지는).
            em.persist(item);
        }else{
            em.merge(item); //UPDATE 비슷한 것.
        }
    }


    public Item findOne(Long id){
        return em.find(Item.class, id);
    }

    public List<Item> findAll(){
        return em.createQuery("select i From Item i",Item.class)
                .getResultList();
    }
}
