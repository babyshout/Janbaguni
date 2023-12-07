package kopo.poly.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Getter
@Data
@JsonDeserialize

public class OcrResultComposite {
    private OrderDTO orderDTO;
//    private CrawlingComposite crawlingComposite;
    private OcrComposite ocrComposite;
    public OcrResultComposite(){}

    public OcrResultComposite(OrderDTO orderDTO, OcrComposite ocrComposite){
        this.orderDTO = orderDTO;
//        this.crawlingComposite = crawlingComposite;
        this.ocrComposite = ocrComposite;
    }

}
