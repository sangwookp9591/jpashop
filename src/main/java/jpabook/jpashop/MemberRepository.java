package jpabook.jpashop;
//Repository 엔티티 같은것을 찾아주는것 -> DAO와 비슷한 역할

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {
    //Spring boot을 쓰기때문에 Spring contanier위에서 모든게 동작한다.
    //Spring boot가 어노테이션 위에 있으면 EntityManager를 주입해준다.
    //spring-boot-starter-data-jpa를 등록하면서 EntityManager 생성하는게 자동으로 들어간다.
    //yml의 jpa설정파일을 다 읽어서 EntityManagerFactory같은 코드가 다 만들어 져버린다. 우리는 사용만 하면된다.
    @PersistenceContext
    private EntityManager em;

    public Long save(Member member){ //shift+ctrl+t
        em.persist(member);
        //왜 Member만 반환하면되는데 member.getId()를 반환하는가?
        //김영한 강사님의 원칙이다 커맨드와 쿼리를 분리하라.
        //저장을 하고나면 가급적이면 사이드 이팩트를 일으키는 커맨드 성이기때문에
        //리턴값을 거의 안만들고 아이디정도만줘서 다시 조회할수 있도록 한다.
        return member.getId();
    }

    public Member find(Long id){
        return em.find(Member.class,id);
    }
}
