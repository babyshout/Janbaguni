package kopo.poly.order.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OcrDTO {
    private String price; // 단가
    private String productName;  // 상품명
    private String unit; // 단위
    private String count; // 수량
    private String userId;
    private String ocrDate;

}
