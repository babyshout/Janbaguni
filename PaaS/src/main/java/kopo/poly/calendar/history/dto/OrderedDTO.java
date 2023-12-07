package kopo.poly.calendar.history.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * mysql myDB 의 ORDERED 의 스키마와 동일함
 */
@Getter
@Setter
@ToString
public class OrderedDTO {
    private int seq;
    private String url;
    private String userId;
    private String ocrDate;
    private String productName;
    private String price;
    private String unit;
    private String count;
}
