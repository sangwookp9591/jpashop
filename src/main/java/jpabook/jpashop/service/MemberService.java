package jpabook.jpashop.service;

import jpabook.jpashop.Repository.MemberRepository;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) //jpa의 모든 데이터 변경이나 로직은 트랜잭션 안에서 다일어나야한다.
//영속섣컨텍스트를 flush안하고 더티체킹을 안해서 성능상 이점이있음.
//읽기 전용이기 때문에 디비한태도 부화를 덜어줄 수 있음.
@RequiredArgsConstructor
public class MemberService {

    //주입하기 까다로움
  //  @Autowired
 //   private MemberRepository memberRepository;

    //setter injection
    //메서드로 주입하기 때문에 가짜 memberRepository를 주입할수 있음
    //단점 -> runtime(실제 application 돌아가는 시점에)에 누군가 이걸 변경 할 수 있음.
//    @Autowired
//    public void setMemberRepository(MemberRepository memberRepository){
//        this.memberRepository = memberRepository;
//    }

    //생성자 주입
//    private final MemberRepository memberRepository;
//
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    //lombok

    private final MemberRepository memberRepository;


    //회원 가입
    @Transactional //jpa의 모든 데이터 변경이나 로직은 트랜잭션 안에서 다일어나야한다.
    public Long join(Member member){
        validateDuplicateMember(member);//중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        //EXCEPTION
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    //회원 전체 조회
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    //회원 단건 조회
    public Member findOne(Long id){
        return memberRepository.findOne(id);
    }


}
