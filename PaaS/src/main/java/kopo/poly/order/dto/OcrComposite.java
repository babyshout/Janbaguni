package kopo.poly.order.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class OcrComposite {
    private List<String> nameList;
    private List<String> unitList;
    private List<String> counstList;
    private List<String> priceList;
    private String date;

    public OcrComposite(List<String> nameList, List<String> unitList,List<String> counstList,List<String> priceList, String date){
        this.nameList = nameList;
        this.unitList = unitList;
        this.counstList = counstList;
        this.priceList = priceList;
        this.date = date;
    }
}
