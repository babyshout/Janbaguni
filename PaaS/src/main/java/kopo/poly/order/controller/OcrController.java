package kopo.poly.order.controller;

import kopo.poly.order.dto.OcrResultComposite;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/order")
public class OcrController {
    @GetMapping("/upload-form")
    public String uploadForm(HttpSession session) throws Exception {
        String userId = (String) session.getAttribute("SS_USER_ID");
        if(userId == null || userId.equals("")){
            return"user/sign-in_sign-up";
        }else{
            return "/order/upload-form"; // Return the name of the HTML template (upload-form.html)
        }
    }


    @GetMapping("/ocr-result")
    public String ocrResult(Model model, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + ".ocrResult Start!");

        OcrResultComposite result = (OcrResultComposite) session.getAttribute("SS_OCR_RESULT");
        String userId = (String)session.getAttribute("SS_USER_ID");
        if(userId == null || userId.equals("")){
            return"user/sign-in_sign-up";
        }
        model.addAttribute("result", result);

        session.removeAttribute("SS_OCR_RESULT");

        log.info(this.getClass().getName() + ".ocrResult End!");
        return "/order/ocr-result";
    }



}
