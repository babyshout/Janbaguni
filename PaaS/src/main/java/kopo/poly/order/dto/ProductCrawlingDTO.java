package kopo.poly.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
@JsonDeserialize
@AllArgsConstructor
public class ProductCrawlingDTO {
    private String price;
    private String productName;

    private String url;
    private String shop;

    public ProductCrawlingDTO(){}
}
