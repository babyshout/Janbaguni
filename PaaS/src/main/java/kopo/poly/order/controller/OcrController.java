package kopo.poly.order.controller;

import kopo.poly.order.dto.OcrResultComposite;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
@Slf4j
@RequiredArgsConstructor
public class OcrController {
//    @Value("${naver.service.template.secretKey}")
//    private String secretKey;

//    private final IOcrService ocrService;
//
//    private final ICrawlingService crawilingService;
//
//    private final IS3UploadService s3UploadService;


    @GetMapping("/upload-form")
    public String uploadForm() throws Exception {
        return "/upload-form"; // Return the name of the HTML template (upload-form.html)
    }


    @GetMapping("/ocr-result")
    public String ocrResult(Model model, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + ".ocrResult Start!");

        OcrResultComposite result = (OcrResultComposite) session.getAttribute("SS_OCR_RESULT");
        model.addAttribute("result", result);

        session.removeAttribute("SS_OCR_RESULT");

        log.info(this.getClass().getName() + ".ocrResult End!");
        return "ocr-result";
    }



}
