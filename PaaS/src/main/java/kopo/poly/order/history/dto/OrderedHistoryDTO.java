package kopo.poly.order.history.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 얘가 받아줘야 되는거
 * 총 금액 (단가 * 수량)
 * 유저이름
 * OCR_DATE
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class OrderedHistoryDTO {
    private LocalDate start_day;
    private LocalDate end_day;
    private String title;


}
