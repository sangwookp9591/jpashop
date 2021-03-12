package jpabook.jpashop.service;

import jpabook.jpashop.repository.MemberRepositoryOld;
import jpabook.jpashop.domain.Member;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class) //Junit 실행할때 스프링이랑 같이 엮어서 사용할래
@SpringBootTest //스프링부트를 띄운 상태로 test를 진행할려면 필요하다.
//이두가지가 있어야 spring이랑 integration 해서 spring boot를 올려서 테스트 할 수 있다.
@Transactional //test case에 한에서 이트랜젝션 걸고 test를 한다음에 테스트가 끝나면 다 롤백해버린다.
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired
    MemberRepositoryOld memberRepository;
    @Autowired EntityManager em;//query나가는 것을 보고싶을 경우(2) -> flush를 사용해 insert되는것만 확인하고 종료시 롤백한다.

    @Test
   // @Rollback(value = false) //query나가는 것을 보고싶을 경우(1) -> 디비에 데이터가 쌓인다! 롤백이되지않아서.
    public void 회원가입() throws Exception{
        //given 이런게 주어졌을때
        Member member = new Member();
        member.setName("kim");

        //when 이렇게하면
        Long id = memberService.join(member);

        //then 이렇게 된다.

        //query나가는 것을 보고싶을 경우(2) ->
        em.flush(); //flush함으로써 영속성 컨텍스트에있는 변경 등록 내용을 반영한다.

        //여기서 저장한 맴버와 repository에서 찾은 애랑 같은 건지 비교한다.
        //같은 영속성 컨텍스트에서 가지고와서 비교
        assertEquals(member,memberRepository.findOne(id));
    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception{
        //given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");
        //when
        memberService.join(member1);
        memberService.join(member2); //예외가 발생해야 한다.

        //then
        fail("예외가 발생해야 한다.");
        //여기까지 오면안된다.
        // 이유-> 이미 위에서 exception이 발생해서 여기까지오면 안되는것
        // 그래서 다시 위에try catch 또는  @Test(expected = IllegalStateException.class)를 해줘야한다.
    }

}