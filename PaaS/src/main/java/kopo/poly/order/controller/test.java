package kopo.poly.order.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class test {

    @GetMapping("/loading/Loading")
    public String loading(){
        return "/loading/Loading";
    }

    @GetMapping("/order/test")
    public String test(){
        return "/order/test";
    }
}
