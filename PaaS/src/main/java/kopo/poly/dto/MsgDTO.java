package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import kopo.poly.community.dto.CommunityDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class MsgDTO {
    private int result;
    private String msg;

    private String item; // 이것저것 담는용 ..

}
