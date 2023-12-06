package kopo.poly.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonDeserialize
public class OrderDTO {
    private String userId;
    private String ocrDate;
    private String url;
}
