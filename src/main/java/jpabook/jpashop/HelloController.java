package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("hello")
    public String hello(Model model){//Model - spring ui에 속함, 얘에다가 데이터를 실어서 뷰에 넘길 수 있다.
        model.addAttribute("data","hello");
        return "hello";//화면이름

    }
}
