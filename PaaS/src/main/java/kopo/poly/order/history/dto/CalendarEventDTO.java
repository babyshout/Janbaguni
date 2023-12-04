package kopo.poly.order.history.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class CalendarEventDTO {
    private LocalDate start_day;
    private LocalDate end_day;
    private String title;
    private OrderedHistoryDTO orderedHistoryDTO;
}
