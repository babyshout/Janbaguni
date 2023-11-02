package kopo.poly.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Getter
public class OcrResultComposite {
    private OrderDTO orderDTO;
//    private CrawlingComposite crawlingComposite;
    private OcrComposite ocrComposite;

    public OcrResultComposite(OrderDTO orderDTO, OcrComposite ocrComposite){
        this.orderDTO = orderDTO;
//        this.crawlingComposite = crawlingComposite;
        this.ocrComposite = ocrComposite;
    }

}
