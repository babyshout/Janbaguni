package kopo.poly.order.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProductCrawlingDTO {
    private String price;
    private String name;

    private String linkUrl;
    private String shop;
}
