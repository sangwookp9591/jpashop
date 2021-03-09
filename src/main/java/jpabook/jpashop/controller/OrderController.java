package jpabook.jpashop.controller;

import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/order")
    public String createForm(Model model){
        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();

        model.addAttribute("members",members);
        model.addAttribute("items",items);

        return "order/orderForm";

    }


    @PostMapping("/order")
    public String order(@RequestParam("memberId") Long memberId,
                        @RequestParam("itemId") Long itemId,
                        @RequestParam("count") int count){
        //@RequestParam은 form submit 방식이다.여기선 선택된 selectBox의 value값과 count값이 매핑되어 바인딩된다.


        orderService.order(memberId, itemId, count);
        // 해당값을 controller에도 설정해도되지만 하지 않는 이유는
        //컨트롤러에서 설정하게되면 controller자체가 지저분해질 뿐 만 아니라,
        //테스트할때도 그렇고 transcation안에서 JPA가 동작할때 제일깔끔하게 동작하기 때문이다.
        //controller에서는 식별자만 넘기고 service 계층에서는 엔티티랑 이런것을 더 의존하기 떄문에
        //안에서 넘기면 더할 수 있는게 많다.
        //주로 command성 주문이런것은 Controller에서 식별자만 넘기고 실제 핵심 비지니스 서비스에서 enity를 찾는것부터는 거기서한다
        //enity의 값들도 transcation 안에서 조회를해야 영속상태로 진행되기 때문에 그럼 상태도 변경할 수 있다.

        //조회는 상관없지만 조회가 아닌 핵심 비지니스로직은 밖에서 넘기는것보다 식별자만 받은뒤 핵심비지니스 로직을 transcaction안에서 할경우
        //영속상태를 유지할 수 있기때문에 주문하면서 뭔가 수정되더라도 변경감지를 적용가능 밖에서 가지고오면 더티체킹 자체가 안됨
        //트랜잭션없이 밖에서 들고온거기때문에.
        return "redirect:/orders";
    }
    @GetMapping("/orders")
    public String orderList(@ModelAttribute("orderSearch")OrderSearch orderSearch,Model model){
        //@ModelAttribute("orderSearch")
        //굳이 ordereSearch에 담지않아도 알아서 담긴다.
        //form submit이된다 즉 model.addAttribute("orderSearch",orderSearch);에 담기는 것이다.
        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders",orders);
        return "order/orderList";
    }


    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId){
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }


}
