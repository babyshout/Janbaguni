package kopo.poly.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;


@Getter
@Setter
@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonDeserialize
@AllArgsConstructor
@NoArgsConstructor
public class OcrDTO {
    private String userId;
    private String ocrDate;
    private String url;

    private String price; // 단가
    private String productName;  // 상품명
    private String unit; // 단위
    private String count; // 수량

}
