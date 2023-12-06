package kopo.poly.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
@JsonDeserialize
@AllArgsConstructor
@NoArgsConstructor
public class MsgDTO {
    private int result;
    private String msg;
}
