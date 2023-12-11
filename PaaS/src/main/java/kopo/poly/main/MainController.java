package kopo.poly.main;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping
public class MainController {

    @GetMapping(value = "/index")
    public String index(){
        log.info("index Start!");

        log.info("index End!");

        return "/main/index";
    }


}
