package kopo.poly.order.controller;

import kopo.poly.order.dto.*;
import kopo.poly.order.service.ICrawlingService;
import kopo.poly.order.service.IOcrService;
import kopo.poly.order.service.IS3UploadService;
import kopo.poly.order.utill.DateUtil;
import kopo.poly.order.utill.SortUtil;
import kopo.poly.order.utill.StringPreprocessingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RestCrawlingController {
    @Value("${naver.service.template.secretKey}")
    private String secretKey;

    private final IOcrService ocrService;

    private final ICrawlingService crawlingService;

    private final IS3UploadService s3UploadService;
    OcrComposite result;

    @PostMapping("/crawlingResult")
    @ResponseBody
    public List<List<ProductCrawlingDTO>> crawlingResult(@RequestBody Map<String, String> requestBody, HttpSession session) {
        CrawlingComposite tmp = (CrawlingComposite) session.getAttribute("SS_CRAWLING_RESULT");
        List<List<ProductCrawlingDTO>> resultList = new ArrayList<>();

        log.info(this.getClass().getName() + ".crawlingResult Start!");

        String source = requestBody.get("source");
        log.info("source : " + source);

        switch (source) {
            case "ace":
                for (List<ProductCrawlingDTO> data : tmp.getAceList()) {
                    resultList.add(data);
                }

                break;
            case "goodFood":
                for (List<ProductCrawlingDTO> data : tmp.getGoodFoodList()) {
                    resultList.add(data);
                }
                break;
            case "babyLeaf":
                for (List<ProductCrawlingDTO> data : tmp.getBabyLeafList()) {
                    resultList.add(data);
                }
                break;
            case "foodEn":
                for (List<ProductCrawlingDTO> data : tmp.getFoodEnList()) {
                    resultList.add(data);
                }
            case "monoMart":
                for (List<ProductCrawlingDTO> data : tmp.getMonoMartList()){
                    resultList.add(data);
                }
        }
        session.setMaxInactiveInterval(1800);
        return resultList;
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
        boolean saveValue = "Y".equals(save); // "Y" 값을 체크로 간주, 그 외의 경우는 false로 설정
        OcrResultComposite resultComposite = null;
        String msg = "";
        int res = 0;
        MsgDTO dto = null;
        MultipartFile copiedFile = null;
        OrderDTO orderDTO = new OrderDTO();

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

            log.info(userId);

            saveFileName = DateUtil.getDateTime("HHmmss") + "." + ext;


/********************************************************************************************************************************/

            File tempFile = File.createTempFile("temp", uploadOcrFile.getOriginalFilename());
            uploadOcrFile.transferTo(tempFile);

            /****************************OCR*******************************/
            String naverSecretKey = secretKey;
            result = ocrService.getOcrResult("POST", tempFile.getPath(), naverSecretKey, ext);


            tempFile.delete();
            if (result.getDate() == null || result.getDate().isEmpty()) {
                tempFile.delete();
            } // Delete the temporary file

            log.info("tempFile.Path : " + tempFile.getPath());


            try {
                log.info("이미지 url db 저장 시작!!!");
                imageUrl = s3UploadService.upload(copiedFile, saveFileName);
                orderDTO.setUserId(userId);
                orderDTO.setUrl(imageUrl);
                orderDTO.setOcrDate(result.getDate());


                for (int i = 0; i < result.getNameList().size(); i++) {
                    OcrDTO ocrDTO = new OcrDTO();
                    ocrDTO.setUserId(userId);
                    ocrDTO.setUrl(imageUrl);
                    ocrDTO.setOcrDate(result.getDate());
                    ocrDTO.setProductName(result.getNameList().get(i));
                    ocrDTO.setPrice(result.getPriceList().get(i));
                    ocrDTO.setCount(result.getCounstList().get(i));
                    ocrDTO.setUnit(result.getUnitList().get(i));

                    res = s3UploadService.insertImageUrl(ocrDTO, saveValue);
                    ocrDTO = null;
                }


                log.info("saveValue : " + save + "!!!!!!!!!");


                log.info("ocrData db저장 시작!!!!!");

                log.info("ocrData db저장 끝!!!!!");


                if (res == 1) {
                    msg = "파일 업로드 성공" + "\n" + "페이지가 이동됩니다.";
                    resultComposite = new OcrResultComposite(orderDTO, result);
                    session.setAttribute("SS_CRAWLING_RESULT", mkListForCrawling());
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

    @PostMapping("searchCrawlingItem")
    @ResponseBody
    public MsgDTO searchCrawlingItem(@RequestParam String searchText, HttpSession session){
        MsgDTO dto = null;
        String msg = "";
        int res = 0;

        if(searchText.equals("")){
            msg = "찾으시는 물품을 입력해주세요.";
            res = 2;
            dto = new MsgDTO();
            dto.setMsg(msg);
            dto.setResult(res);
            return dto;
        } else if (searchText == null) {
            msg = "알 수 없는 오류가 발생하였습니다. \n다시 시도해주세요.";
            res = 2;
            dto = new MsgDTO();
            dto.setResult(res);
            dto.setMsg(msg);
            return dto;
        }else{
            SearchCrawlingComposite searchCrawlingComposite = searchCrawling(searchText);
        
            session.setAttribute( "SS_SEARCH_CRAWLING",searchCrawlingComposite);
            msg = "찾으시는 물품의 최저가를 찾아봤어요!";
            res = 1;
            dto.setMsg(msg);
            dto.setResult(res);
            return dto;
        }
    }


    private CrawlingComposite mkListForCrawling() throws IOException {
        List<List<ProductCrawlingDTO>> acePriceList = new ArrayList<>();
        List<List<ProductCrawlingDTO>> goodFoodList = new ArrayList<>();
        List<List<ProductCrawlingDTO>> babyLeafList = new ArrayList<>();
        List<List<ProductCrawlingDTO>> foodEnList = new ArrayList<>();
        List<List<ProductCrawlingDTO>> monoMartList = new ArrayList<>();


        /**ocr 데이터 크롤링**/
        SortUtil sortUtil = new SortUtil();
        if (result.getDate() != null && !result.getDate().equals("")) { //OCR 데이터 유효성 검사
            for (int i = 0; i < result.getNameList().size(); i++) {
                if (crawlingService.getAceData(result.getNameList().get(i)) != null) {
                    acePriceList.add(crawlingService.getAceData(result.getNameList().get(i)));
                }
                if (crawlingService.getGoodFood(result.getNameList().get(i)) != null) {
                    goodFoodList.add(crawlingService.getGoodFood(result.getNameList().get(i)));
                }
                if (crawlingService.getBabyleaf(result.getNameList().get(i)) != null) {
                    babyLeafList.add(crawlingService.getBabyleaf(result.getNameList().get(i)));
                }
                if (crawlingService.getFoodEn(result.getNameList().get(i)) != null) {
                    foodEnList.add(crawlingService.getFoodEn(result.getNameList().get(i)));
                }
                if (crawlingService.getMonoMart(result.getNameList().get(i)) != null) {
                    monoMartList.add(crawlingService.getMonoMart(result.getNameList().get(i)));
                }
            }
        }

        CrawlingComposite crawlingComposite = new CrawlingComposite(acePriceList, goodFoodList, babyLeafList, foodEnList, monoMartList);
        // getPrice 기준으로 정렬
        sortUtil.sortCrawlingComposite(crawlingComposite);
        acePriceList = null;
        goodFoodList = null;
        babyLeafList = null;
        foodEnList = null;
        monoMartList = null;
        sortUtil = null;
        return crawlingComposite;
    }

    private SearchCrawlingComposite searchCrawling(String keyWord){
        SearchCrawlingComposite searchCrawlingComposite = null;
        SortUtil sortUtil = new SortUtil();
        List<ProductCrawlingDTO> searchAce = new ArrayList<>();
        List<ProductCrawlingDTO> searchGoodFood = new ArrayList<>();
        List<ProductCrawlingDTO> searchBabyLeaf = new ArrayList<>();
        List<ProductCrawlingDTO> searchFoodEn = new ArrayList<>();
        List<ProductCrawlingDTO> searchMonoMart = new ArrayList<>();
        try{
            if(crawlingService.getAceData(keyWord) != null){
                searchAce = crawlingService.getAceData(keyWord);
            }
            if(crawlingService.getGoodFood(keyWord) != null){
                searchGoodFood = crawlingService.getGoodFood(keyWord);
            }
            if(crawlingService.getGoodFood(keyWord) != null){
                searchBabyLeaf = crawlingService.getBabyleaf(keyWord);
            }
            if(crawlingService.getFoodEn(keyWord) != null){
                searchFoodEn = crawlingService.getFoodEn(keyWord);
            }
            if(crawlingService.getMonoMart(keyWord) != null){
                searchMonoMart = crawlingService.getMonoMart(keyWord);
            }

            searchAce = sortUtil.sortSearchCrawlingComposite(searchAce);
            searchGoodFood = sortUtil.sortSearchCrawlingComposite(searchGoodFood);
            searchBabyLeaf = sortUtil.sortSearchCrawlingComposite(searchBabyLeaf);
            searchFoodEn = sortUtil.sortSearchCrawlingComposite(searchFoodEn);
            searchMonoMart = sortUtil.sortSearchCrawlingComposite(searchMonoMart);
            searchCrawlingComposite = new SearchCrawlingComposite(searchAce, searchGoodFood, searchBabyLeaf, searchFoodEn, searchMonoMart);


        }catch (Exception e){
            log.info("써치크롤링~~");
        }finally {
            searchAce = null;
            searchGoodFood = null;
            searchBabyLeaf = null;
            searchFoodEn = null;
            searchMonoMart = null;
            return searchCrawlingComposite;
        }

    }
}
