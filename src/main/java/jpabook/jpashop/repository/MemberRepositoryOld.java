package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor//Autowired가 사용가는하다는 것은
public class MemberRepositoryOld {
    //@PersistenceContext
    // 1. spring이 em 을만들어서 여기다가 주입해주게 된다.
    //@Autowired
    // 2.스프링 부트라서 @PersistenceContext를 안쓰고  @Autowired가 사용가능 하다.
    private final EntityManager em;

    public void save(Member member){
        em.persist(member);
    }

    public Member findOne(Long id){
        return em.find(Member.class,id);
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList(); //ctrl_alt_ n 합쳐서 인라인 만들기
    }

    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name =:name",Member.class)
                .setParameter("name",name)
                .getResultList();
    }
 }
