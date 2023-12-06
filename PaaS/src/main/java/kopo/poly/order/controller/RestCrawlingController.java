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
//import org.springframework.cloud.context.config.annotation.RefreshScope;
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
@RequestMapping("/order")
//@RefreshScope
public class RestCrawlingController {
    @Value("${naver.service.template.secretKey}")
    private String secretKey;

    private final IOcrService ocrService;

    private final ICrawlingService crawlingService;

    private final IS3UploadService s3UploadService;
    OcrComposite result;


    @PostMapping("/crawlingResult")
    public List<List<ProductCrawlingDTO>> crawlingResult(@RequestBody Map<String, String> requestBody, HttpSession session) {
        CrawlingComposite tmp = (CrawlingComposite) session.getAttribute("SS_CRAWLING_RESULT");
//        session.removeAttribute("SS_CRAWLING_RESULT");
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
                break;
            case "monoMart":
                for (List<ProductCrawlingDTO> data : tmp.getMonoMartList()) {
                    resultList.add(data);
                }
                break;
            case "best":
                resultList.add(tmp.getBestList());

        }

        return resultList;
    }

    @PostMapping("/searchCrawlingResult")
    @ResponseBody
    public List<ProductCrawlingDTO> searchResult(@RequestBody Map<String, String> requestBody, HttpSession session) {
        log.info(this.getClass().getName() + ".searchResult Start!!");
        SearchCrawlingComposite crawlingComposite = (SearchCrawlingComposite) session.getAttribute("SS_SEARCH_CRAWLING");
        log.info("포스트다~~~~" + crawlingComposite.getSearchAce().get(0).getPrice());
        List<ProductCrawlingDTO> resultList = new ArrayList<>();
        String source = requestBody.get("source");

        switch (source) {
            case "ace":
                resultList = crawlingComposite.getSearchAce();
                log.info("리절트 리스트~~~" + resultList.get(0).getPrice());
                break;
            case "goodFood":
                resultList = crawlingComposite.getSearchGoodFood();
                break;
            case "babyLeaf":
                resultList = crawlingComposite.getSearchBabyLeaf();
                break;
            case "foodEn":
                resultList = crawlingComposite.getSearchFoodEn();
                break;
            case "monoMart":
                resultList = crawlingComposite.getSearchMonoMart();
                break;

        }
        log.info(this.getClass().getName() + ".searchResult End!!");
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
            } // 임시파일 삭제

            log.info("tempFile.Path : " + tempFile.getPath());


            try {
                log.info("이미지 url db 저장 시작!!!");
                imageUrl = s3UploadService.upload(copiedFile, saveFileName); //Object Storage에 업로드
                orderDTO.setUserId(userId);
                orderDTO.setUrl(imageUrl);
                orderDTO.setOcrDate(result.getDate());


                for (int i = 0; i < result.getNameList().size(); i++) {
                    OcrDTO ocrDTO = new OcrDTO();
                    ocrDTO.setUserId(userId);
                    ocrDTO.setUrl(imageUrl); // Object Storage 경로
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


    @PostMapping("/searchCrawlingItem")
    public MsgDTO searchCrawlingItem(@RequestParam String searchText, HttpSession session) {
        log.info(this.getClass().getName() + ".searchCrawlingItem Start!!");
        MsgDTO dto = null;
        String msg = "";
        int res = 0;

        if (searchText.equals("")) {
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
        } else {
            SearchCrawlingComposite searchCrawlingComposite = searchCrawling(searchText);
            log.info("서치크롤링컴파짓:" + searchCrawlingComposite.toString());
            msg = "찾으시는 물품의 최저가를 찾아봤어요!";
            res = 1;
            dto = new MsgDTO();
            dto.setMsg(msg);
            dto.setResult(res);
            session.setAttribute("SS_SEARCH_CRAWLING", searchCrawlingComposite);
            SearchCrawlingComposite tmp = (SearchCrawlingComposite) session.getAttribute("SS_SEARCH_CRAWLING");
            log.info(tmp.getSearchAce().get(0).getPrice());
            return dto;
        }
    }


    private CrawlingComposite mkListForCrawling() throws IOException {
        List<List<ProductCrawlingDTO>> acePriceList = new ArrayList<>();
        List<List<ProductCrawlingDTO>> goodFoodList = new ArrayList<>();
        List<List<ProductCrawlingDTO>> babyLeafList = new ArrayList<>();
        List<List<ProductCrawlingDTO>> foodEnList = new ArrayList<>();
        List<List<ProductCrawlingDTO>> monoMartList = new ArrayList<>();

        List<List<ProductCrawlingDTO>> bestList = new ArrayList<>();

        List<ProductCrawlingDTO> bestResultList = new ArrayList<>();

        List<ProductCrawlingDTO> aceTemp = new ArrayList<>();
        List<ProductCrawlingDTO> goodFoodTemp = new ArrayList<>();
        List<ProductCrawlingDTO> babyleafTemp = new ArrayList<>();
        List<ProductCrawlingDTO> foodEnTemp = new ArrayList<>();
        List<ProductCrawlingDTO> monoMartTemp = new ArrayList<>();

        /**ocr 데이터 크롤링**/
        SortUtil sortUtil = new SortUtil();
        if (result.getDate() != null && !result.getDate().equals("")) { //OCR 데이터 유효성 검사

            for (int i = 0; i < result.getNameList().size(); i++) {
                if (crawlingService.getAceData(result.getNameList().get(i)) != null) {
                    aceTemp = crawlingService.getAceData(result.getNameList().get(i));
                    acePriceList.add(aceTemp);
                    bestList.add(aceTemp);
                }
                if (crawlingService.getGoodFood(result.getNameList().get(i)) != null) {
                    goodFoodTemp = crawlingService.getGoodFood(result.getNameList().get(i));
                    goodFoodList.add(goodFoodTemp);
                    bestList.add(goodFoodTemp);
                }
                if (crawlingService.getBabyleaf(result.getNameList().get(i)) != null) {
                    babyleafTemp = crawlingService.getBabyleaf(result.getNameList().get(i));
                    babyLeafList.add(babyleafTemp);
                    bestList.add(babyleafTemp);
                }
                if (crawlingService.getFoodEn(result.getNameList().get(i)) != null) {
                    foodEnTemp = crawlingService.getFoodEn(result.getNameList().get(i));
                    foodEnList.add(foodEnTemp);
                    bestList.add(foodEnTemp);
                }
                if (crawlingService.getMonoMart(result.getNameList().get(i)) != null) {
                    monoMartTemp = crawlingService.getMonoMart(result.getNameList().get(i));
                    monoMartList.add(monoMartTemp);
                    bestList.add(monoMartTemp);
                }
                sortUtil.sortProductLists(bestList);

            }


            for(int i = 0; i < bestList.size(); i++){
                if(bestList.get(i).size() > 0 && bestList.get(i).get(0) != null)
                    bestResultList.add(bestList.get(i).get(0));
            }
        }
        CrawlingComposite crawlingComposite = new CrawlingComposite(acePriceList, goodFoodList, babyLeafList, foodEnList, monoMartList, bestResultList);

        // getPrice 기준으로 정렬
        sortUtil.sortCrawlingComposite(crawlingComposite);
        acePriceList = null;
        goodFoodList = null;
        babyLeafList = null;
        foodEnList = null;
        monoMartList = null;

        sortUtil = null;

        bestResultList = null;
        bestList = null;
        aceTemp = null;
        goodFoodTemp = null;
        babyleafTemp = null;
        foodEnTemp = null;
        monoMartTemp = null;
        return crawlingComposite;
    }

    private SearchCrawlingComposite searchCrawling(String keyWord) {
        SearchCrawlingComposite searchCrawlingComposite = null;
        SortUtil sortUtil = new SortUtil();
        List<ProductCrawlingDTO> searchAce = new ArrayList<>();
        List<ProductCrawlingDTO> searchGoodFood = new ArrayList<>();
        List<ProductCrawlingDTO> searchBabyLeaf = new ArrayList<>();
        List<ProductCrawlingDTO> searchFoodEn = new ArrayList<>();
        List<ProductCrawlingDTO> searchMonoMart = new ArrayList<>();
        try {
            if (crawlingService.getAceData(keyWord) != null) {
                searchAce = sortUtil.sortProductList(crawlingService.getAceData(keyWord));
            }
            if (crawlingService.getGoodFood(keyWord) != null) {
                searchGoodFood = sortUtil.sortProductList(crawlingService.getGoodFood(keyWord));
            }
            if (crawlingService.getGoodFood(keyWord) != null) {
                searchBabyLeaf = sortUtil.sortProductList(crawlingService.getBabyleaf(keyWord));
            }
            if (crawlingService.getFoodEn(keyWord) != null) {
                searchFoodEn = sortUtil.sortProductList(crawlingService.getFoodEn(keyWord));
            }
            if (crawlingService.getMonoMart(keyWord) != null) {
                searchMonoMart = sortUtil.sortProductList(crawlingService.getMonoMart(keyWord));
            }


            for (int i = 0; i < searchAce.size(); i++) {
                log.info(i + "번째 : " + searchAce.get(i).getPrice());
            }
            searchCrawlingComposite = new SearchCrawlingComposite(searchAce, searchGoodFood, searchBabyLeaf, searchFoodEn, searchMonoMart);


        } catch (Exception e) {
            log.info("써치크롤링~~");
            e.printStackTrace();
        } finally {
            searchAce = null;
            searchGoodFood = null;
            searchBabyLeaf = null;
            searchFoodEn = null;
            searchMonoMart = null;
            log.info("리턴시키기 전에 확인해봄~~:" + searchCrawlingComposite.getSearchAce().get(0).getPrice());
            return searchCrawlingComposite;
        }

    }
}
