package kopo.poly.order.controller;

import kopo.poly.dto.MsgDTO;
import kopo.poly.order.dto.*;
import kopo.poly.order.dto.CrawlingComposite;
import kopo.poly.order.dto.OcrComposite;
import kopo.poly.order.dto.ImageDTO;
import kopo.poly.order.dto.ProductCrawlingDTO;
import kopo.poly.order.service.ICrawlingService;
import kopo.poly.order.service.IOcrService;
import kopo.poly.order.service.IS3UploadService;
import kopo.poly.order.utill.SortUtil;
import kopo.poly.order.utill.StringPreprocessingUtil;
import kopo.poly.order.utill.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

@Controller
@Slf4j
@RequiredArgsConstructor
public class OcrController {
    @Value("${naver.service.template.secretKey}")
    private String secretKey;

    private final IOcrService ocrService;

    private final ICrawlingService crawilingService;

    private final IS3UploadService s3UploadService;


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


    /**
     * OCR & 크롤링 수행
     *
     * @param uploadOcrFile
     * @param save
     * @param session
     * @return
     * @throws IOException
     */
    @PostMapping("/uploadAndOcr")
    @ResponseBody
    public MsgDTO uploadAndOcr(@RequestParam("image") MultipartFile uploadOcrFile,
                               @RequestParam(value = "save", required = false, defaultValue = "N") String save, HttpSession session
                               ) throws IOException {

        log.info(this.getClass().getName() + "uploadAndOcr Start");

        OcrResultComposite resultComposite = null;
        String msg = "";
        int res = 0;
        MsgDTO dto = null;
        MultipartFile copiedFile = null;
        ImageDTO orderDTO = new ImageDTO();

        log.info("save : " + save);


        /*********MultipartFile 복제 생성*********/
        try {
            byte[] fileData = uploadOcrFile.getBytes();

            copiedFile = new MockMultipartFile(uploadOcrFile.getOriginalFilename(), uploadOcrFile.getOriginalFilename(), uploadOcrFile.getContentType(), fileData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringPreprocessingUtil stringUtil = new StringPreprocessingUtil();
        String originFileName = uploadOcrFile.getOriginalFilename();
        String ext = stringUtil.getExt(originFileName);


        log.info("originFileName : " + originFileName);
        log.info("ext : " + ext);
        log.info("uploadOcrFile.getName() : " + uploadOcrFile.getName());

        String saveFileName;
        String imageUrl;


        if (uploadOcrFile.isEmpty() || uploadOcrFile == null) {
            msg = "이미지 파일을 업로드해주세요";
            res = 2;
            dto = new MsgDTO();
            dto.setResult(res);
            dto.setMsg(msg);
            return dto;
        } else {
            String userId = Optional.ofNullable((String) session.getAttribute("SS_USER_ID")).orElseGet(String::new);

            if (userId.equals("")) {
                userId = "user01";
            }

            saveFileName = DateUtil.getDateTime("HHmmss") + "." + ext;


/********************************************************************************************************************************/

            File tempFile = File.createTempFile("temp", uploadOcrFile.getOriginalFilename());
            uploadOcrFile.transferTo(tempFile);

            /****************************OCR*******************************/
            String naverSecretKey = secretKey;
            OcrComposite result = ocrService.getOcrResult("POST", tempFile.getPath(), naverSecretKey, ext);
            OcrDTO ocrDTO = new OcrDTO();


            tempFile.delete();
            if (result.getDate() == null || result.getDate().isEmpty()) {
                tempFile.delete();
            } // Delete the temporary file

            log.info("tempFile.Path : " + tempFile.getPath());


            List<List<ProductCrawlingDTO>> acePriceList = new ArrayList<>();
            List<List<ProductCrawlingDTO>> goodFoodList = new ArrayList<>();
            List<List<ProductCrawlingDTO>> babyLeafList = new ArrayList<>();

            /**ocr 데이터 크롤링**/
            SortUtil sortUtil = new SortUtil();
            if (result.getDate() != null && !result.getDate().equals("")) { //OCR 데이터 유효성 검사
                for (int i = 0; i < result.getNameList().size(); i++) {
                    if (crawilingService.getAceData(result.getNameList().get(i)) != null) {
                        acePriceList.add(crawilingService.getAceData(result.getNameList().get(i)));
                    }
                    if (crawilingService.getGoodFood(result.getNameList().get(i)) != null) {
                        goodFoodList.add(crawilingService.getGoodFood(result.getNameList().get(i)));
                    }
                    if (crawilingService.getBabyleaf(result.getNameList().get(i)) != null) {
                        babyLeafList.add(crawilingService.getBabyleaf(result.getNameList().get(i)));
                    }
                }


                try {
                    log.info("이미지 url db 저장 시작!!!");
                    imageUrl = s3UploadService.upload(copiedFile, saveFileName);

                    orderDTO.setUserId(userId);
                    orderDTO.setUrl(imageUrl);
                    orderDTO.setOcrDate(result.getDate());
                    boolean saveValue = "Y".equals(save); // "Y" 값을 체크로 간주, 그 외의 경우는 false로 설정
                    log.info("saveValue : " + save + "!!!!!!!!!");

                    res = s3UploadService.insertImageUrl(orderDTO, saveValue);

                    log.info("ocrData db저장 시작!!!!!");
//                    ocrService.insertOrder(ocrDTO, result);
                    log.info("ocrData db저장 끝!!!!!");
                    CrawlingComposite crawlingComposite = new CrawlingComposite(acePriceList, goodFoodList, babyLeafList);
                    // getPrice 기준으로 정렬
                    sortUtil.sortCrawlingComposite(crawlingComposite);

                    if (res == 1) {
                        msg = "파일 업로드 성공" + "\n" + "잠시만 기다려주세요.";
                        resultComposite = new OcrResultComposite(orderDTO,crawlingComposite, result);
                    } else {
                        msg = "파일 업로드 실패";
                    }
                    log.info(String.valueOf(saveValue));
                } catch (Exception e) {
                    msg = "실패하였습니다." + e;
                    e.printStackTrace();
                } finally {
                    dto = new MsgDTO();
                    dto.setResult(res);
                    dto.setMsg(msg);
                }
            }

            /**세션에 최종값 넣음**/
            session.setAttribute("SS_OCR_RESULT", resultComposite);
            log.info(this.getClass().getName() + ".uploadAndOcr End!!!!!!!!!!!!!!!!");
            return dto;
        }
    }
}
