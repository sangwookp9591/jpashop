package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member,Long> {//JpaRepository<type,pk type>
    //이걸 JPQL로 구현할 필요가없다. 시그니처를 보고 알아서 해결해준다.
    //여기는 룰이있는데 findBy + Name을 하게되면
    //select m from Member m where m.name = ? 이라고 알아서 짜버린다. Name을보고 만들기 떄문에 name이 중요하다
    List<Member> findByName(String name);
}
