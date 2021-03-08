package jpabook.jpashop.api;


import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController //@Controller + @ResponseBody
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public CreateMemberResponse savewMemberV1(@RequestBody @Valid Member memmber){//javax validation 관련된 것들 자동 validation
        //@RequestBody JSON으로 온 BODY를 MEMBER에 그대로 매핑해서 넣어준다.
        //즉 JSON DATA를 MEMBER로 바꿔준다.
        Long id = memberService.join(memmber);
        return new CreateMemberResponse(id);

    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request){

        memberService.update(id,request.getName());//update 후 트랜잭션 끝남 (command  단)
        Member findMember = memberService.findOne(id);//정상적으로 잘 반영됫는지 쿼리를해서 가져옴(쿼리 단)
        return new UpdateMemberResponse(findMember.getId(), findMember.getName()); //Response DTO를 통해 반환

    }

    @Data
    private class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }
}
