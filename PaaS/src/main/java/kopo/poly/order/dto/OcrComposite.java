package kopo.poly.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Getter
@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonDeserialize
public class OcrComposite {
    private List<String> nameList;
    private List<String> unitList;
    private List<String> counstList;
    private List<String> priceList;
    private String date;

    public OcrComposite(){}
    public OcrComposite(List<String> nameList, List<String> unitList,List<String> counstList,List<String> priceList, String date){
        this.nameList = nameList;
        this.unitList = unitList;
        this.counstList = counstList;
        this.priceList = priceList;
        this.date = date;
    }
}
