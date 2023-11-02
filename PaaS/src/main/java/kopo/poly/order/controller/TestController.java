package kopo.poly.order.controller;

import kopo.poly.order.dto.ProductCrawlingDTO;
import kopo.poly.order.service.ICrawlingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TestController {
    private final ICrawlingService crawlingService;

    @GetMapping("test")
    public String getCrawling() {
        try {
            int i = 0;
            List<ProductCrawlingDTO> dto = crawlingService.getMonoMart("소스");
            if (dto.size() != 0) {
                for (ProductCrawlingDTO p : dto) {
                    log.info(i + ". : " + p.getPrice());
                    i++;
                }

            } else {
                log.info("dto가 null임");
            }

        } catch (Exception e) {
            log.info("error");
            e.printStackTrace();
        }
        return "test";
    }
}
