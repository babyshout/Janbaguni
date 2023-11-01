package kopo.poly.order.service;

import kopo.poly.order.dto.OcrComposite;
import kopo.poly.order.dto.OcrDTO;

public interface IOcrService {
//    public List<String> getOcrResult(String type, String filePath, String naver_secretKey, String ext);
    OcrComposite getOcrResult(String type, String filePath, String naver_secretKey, String ext);

//    public void insertOrder(OcrDTO pDTO, OcrComposite ocrComposite) throws Exception;


}
