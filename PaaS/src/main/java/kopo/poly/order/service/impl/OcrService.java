package kopo.poly.order.service.impl;

import kopo.poly.order.dto.OcrComposite;

import kopo.poly.order.dto.OcrDTO;
import kopo.poly.order.infra.naver.ocr.NaverOrderPaperOcr;
//import kopo.poly.order.persistance.mapper.IOrderPaperMapper;
import kopo.poly.order.service.IOcrService;
import kopo.poly.order.utill.StringPreprocessingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OcrService implements IOcrService {
    private final NaverOrderPaperOcr naverApi;
//    private final IOrderPaperMapper orderPaperMapper;

    @Override
    public OcrComposite getOcrResult(String type, String filePath, String naver_secretKey, String ext) {
        log.info(this.getClass().getName() + "getOcrResult Start!");
        StringPreprocessingUtil stringUtil = new StringPreprocessingUtil();

        // Clova 호출
        List<String> result = naverApi.callApi(type, filePath, naver_secretKey, ext);
        List<String> nameList = stringUtil.parseStringList(result, 0);
        List<String> tmpUnitList = stringUtil.splitString(result, 1);
        List<String> countList = stringUtil.parseStringList(result, 2);
        List<String> priceList = stringUtil.parseStringList(result, 3);


        String tmpDate = stringUtil.parseStringList(result, 4).get(0);

        String date = stringUtil.replaceDate(tmpDate);
        List<String> unitList = stringUtil.replaceUnit(tmpUnitList);


        OcrComposite ocrComposite = new OcrComposite(nameList, unitList, countList,priceList, date);

        return ocrComposite;
    }

//    public void insertOrder(OcrDTO pDTO, OcrComposite ocrComposite) throws Exception{
//        log.info(this.getClass().getName() + ".insertOrder Start!");
//
//        for(int i = 0; i < ocrComposite.getNameList().size(); i++){
//            pDTO.setProductName(ocrComposite.getNameList().get(i));
//            pDTO.setUnit(ocrComposite.getUnitList().get(i));
//            pDTO.setPrice(ocrComposite.getPriceList().get(i));
//            pDTO.setCount(ocrComposite.getCounstList().get(i));
//
//            orderPaperMapper.insertOredr(pDTO);
//            pDTO = null;
//        }
//
//
//    }
}

