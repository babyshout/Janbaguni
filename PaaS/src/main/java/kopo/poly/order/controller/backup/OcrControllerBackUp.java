//package kopo.poly.order.controller;
//
//import kopo.poly.order.dto.*;
//import kopo.poly.order.dto.CrawlingComposite;
//import kopo.poly.order.dto.OcrComposite;
//import kopo.poly.order.dto.OrderDTO;
//import kopo.poly.order.dto.ProductDTO;
//import kopo.poly.order.service.ICrawlingService;
//import kopo.poly.order.service.IOcrService;
//import kopo.poly.order.service.IS3UploadService;
//import kopo.poly.order.utill.SortUtil;
//import kopo.poly.order.utill.StringPreprocessingUtil;
//import kopo.poly.order.utill.DateUtil;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.servlet.http.HttpSession;
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
//
//@Controller
//@Slf4j
//@RequiredArgsConstructor
//public class OcrControllerBackUp {
//    @Value("${naver.service.template.secretKey}")
//    private String secretKey;
//
//    private final IOcrService ocrService;
//
//    private final ICrawlingService crawilingService;
//
//    private final IS3UploadService s3UploadService;
//
//
//    @GetMapping("/upload-form")
//    public String uploadForm() throws Exception {
//        return "/upload-form"; // Return the name of the HTML template (upload-form.html)
//    }
//
//
//    @GetMapping("/ocr-result")
//    public String ocrResult(HttpSession session)throws Exception{
//
//
//        return "ocr-result";
//    }
//
//    /**
//     * OCR & 크롤링 수행
//     * @param uploadOcrFile
//     * @param model
//     * @param session
//     * @return
//     * @throws IOException
//     */
//    @PostMapping("/uploadAndOcr")
//    public String uploadAndOcr(@RequestParam("uploadOcrFile") MultipartFile uploadOcrFile,
//                               @RequestParam(value = "save", required = false, defaultValue = "N") String save,
//                               Model model, HttpSession session) throws IOException {
//
//        log.info(this.getClass().getName() + "uploadAndOcr Start");
//        MultipartFile copiedFile = null;
//        OrderDTO orderDTO = new OrderDTO();
//
//        log.info("save : " + save);
//
//
//        /*********MultipartFile 복제 생성*********/
//        try{
//            byte[] fileData = uploadOcrFile.getBytes();
//
//            copiedFile = new MockMultipartFile(
//                    uploadOcrFile.getOriginalFilename(),
//                    uploadOcrFile.getOriginalFilename(),
//                    uploadOcrFile.getContentType(),
//                    fileData
//            );
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        StringPreprocessingUtil stringUtil = new StringPreprocessingUtil();
//        String originFileName = uploadOcrFile.getOriginalFilename();
//        String ext = stringUtil.getExt(originFileName);
//
//
//        log.info("originFileName : " + originFileName);
//        log.info("ext : " + ext);
//        log.info("uploadOcrFile.getName() : " + uploadOcrFile.getName());
//
//        String saveFileName;
//        String imageUrl;
//
//
//        if (uploadOcrFile.isEmpty()) {
//            return "error";
//        }
//
//        String userId = Optional.ofNullable(
//                (String) session.getAttribute("SS_USER_ID")
//        ).orElseGet(String::new);
//
//        if (userId.equals("")) {
//            userId = "user01";
//        }
//
//        saveFileName = DateUtil.getDateTime("HHmmss") + "." + ext;
//
//
///********************************************************************************************************************************/
//
//        File tempFile = File.createTempFile("temp", uploadOcrFile.getOriginalFilename());
//        uploadOcrFile.transferTo(tempFile);
//
//        /****************************OCR*******************************/
//        String naverSecretKey = secretKey;
//        OcrComposite result = ocrService.getOcrResult("POST", tempFile.getPath(), naverSecretKey, ext);
//        tempFile.delete();
//        if (result.getDate() == null || result.getDate().isEmpty()) {tempFile.delete();} // Delete the temporary file
//
//        log.info("tempFile.Path : " + tempFile.getPath());
//
//
//        List<List<ProductDTO>> acePriceList = new ArrayList<>();
//        List<List<ProductDTO>> goodFoodList = new ArrayList<>();
//        List<List<ProductDTO>> babyLeafList = new ArrayList<>();
//
//        /**ocr 데이터 크롤링**/
//        SortUtil sortUtil = new SortUtil();
//        if (result.getDate() != null && !result.getDate().equals("")) { //OCR 데이터 유효성 검사
//            for (int i = 0; i < result.getNameList().size(); i++) {
//                if (crawilingService.getAceData(result.getNameList().get(i)) != null) {
//                    acePriceList.add(crawilingService.getAceData(result.getNameList().get(i)));
//                }
//                if (crawilingService.getGoodFood(result.getNameList().get(i)) != null) {
//                    goodFoodList.add(crawilingService.getGoodFood(result.getNameList().get(i)));
//                }
//                if (crawilingService.getBabyleaf(result.getNameList().get(i)) != null) {
//                    babyLeafList.add(crawilingService.getBabyleaf(result.getNameList().get(i)));
//                }
//            }
//        }
//        /***********S3 업로드*********/
//        try {
//            log.info("이미지 url db 저장 시작!!!");
//            imageUrl = s3UploadService.upload(copiedFile, saveFileName);
//
//            orderDTO.setUserId(userId);
//            orderDTO.setUrl(imageUrl);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }finally {
//            log.info("이미지 url db 저장 완료!!!!");
//        }
//
//
//        orderDTO.setOcrDate(result.getDate());
//        boolean saveValue = "Y".equals(save); // "Y" 값을 체크로 간주, 그 외의 경우는 false로 설정
//        log.info("saveValue : "+ save + "!!!!!!!!!");
//        try {
//            s3UploadService.insertImageUrl(orderDTO, saveValue);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        model.addAttribute("orderDTO", orderDTO);
//        model.addAttribute("ocrComposite", result);
//        CrawlingComposite crawlingComposite = new CrawlingComposite(acePriceList, goodFoodList, babyLeafList);
//
//        // getPrice 기준으로 정렬
//        sortUtil.sortCrawlingComposite(crawlingComposite);
//
//        model.addAttribute("crawlingComposite", crawlingComposite);
//
//
//
//
//        log.info(String.valueOf(saveValue));
//
//
//
//        log.info(this.getClass().getName() + ".uploadAndOcr End!!!!!!!!!!!!!!!!");
//
//        return "ocr-result"; // Return the name of the HTML template to display the OCR result
//    }
//}
