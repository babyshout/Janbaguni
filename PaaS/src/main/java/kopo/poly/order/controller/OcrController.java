package kopo.poly.order.controller;

import kopo.poly.order.dto.OcrResultComposite;
import kopo.poly.order.dto.SearchCrawlingComposite;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.cloud.context.config.annotation.RefreshScope;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/order")
//@RefreshScope
public class OcrController {
    @GetMapping("/upload-form")
    public String uploadForm(HttpSession session) throws Exception {
        String userId = (String) session.getAttribute("SS_USER_ID");
        if(userId == null || userId.equals("")){
            return"redirect:/login/login-form";
        }else{
            return "/order/upload-form"; // Return the name of the HTML template (upload-form.html)
        }

    }


    @GetMapping("/ocr-result")
    public String ocrResult(Model model, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + ".ocrResult Start!");
        String userId = (String)session.getAttribute("SS_USER_ID");
        if(userId == null || userId.equals("")){
            session.removeAttribute("SS_OCR_RESULT");
            return"redirect:/login/login-form";
        }
        OcrResultComposite result = (OcrResultComposite) session.getAttribute("SS_OCR_RESULT");

        model.addAttribute("result", result);
        if(session.getAttribute("SS_OCR_RESULT") != null){
            session.removeAttribute("SS_OCR_RESULT");
        }


        log.info(this.getClass().getName() + ".ocrResult End!");
        return "/order/ocr-result";
    }

    @GetMapping("searchResult")
    public String searchResult(Model model, HttpSession session) throws Exception{
        log.info(this.getClass().getName() + ".searchResult Start!!!!");
        String userId = (String)session.getAttribute("SS_USER_ID");
        if(userId == null || userId.equals("")){
            session.removeAttribute("SS_SEARCH_CRAWLING");
            return"redirect:/login/login-form";
        }
        SearchCrawlingComposite result = (SearchCrawlingComposite) session.getAttribute("SS_SEARCH_CRAWLING");

        model.addAttribute("result", result);


        log.info(this.getClass().getName() + ".searchResult End!!!!");

        return "/order/searchResult";
    }


}
