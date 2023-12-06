package kopo.poly.calendar.history.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalendarEventDTO {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonProperty(value = "start")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate start;

//    @DateTimeFormat(pattern = "yyyy-MM-dd")
//    @JsonProperty(value = "start")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
//    private LocalDate end;

    private String title;

    private String url;

    private boolean allDay = true;

    private OrderedHistoryDTO orderedHistoryDTO;
}
