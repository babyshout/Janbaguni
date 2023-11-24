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
public class CrawlingComposite {
    private List<List<ProductCrawlingDTO>> aceList;
    private List<List<ProductCrawlingDTO>> goodFoodList;
    private List<List<ProductCrawlingDTO>> babyLeafList;

    private List<List<ProductCrawlingDTO>> foodEnList;

    private List<List<ProductCrawlingDTO>> monoMartList;

    private List<ProductCrawlingDTO> bestList;

    public CrawlingComposite(){}

    public CrawlingComposite(List<List<ProductCrawlingDTO>> aceList, List<List<ProductCrawlingDTO>> goodFoodList,
                             List<List<ProductCrawlingDTO>> babyLeafList, List<List<ProductCrawlingDTO>> foodEnList,
                             List<List<ProductCrawlingDTO>> monoMartList, List<ProductCrawlingDTO> bestList){
        this.aceList = aceList;
        this.babyLeafList = babyLeafList;
        this.goodFoodList = goodFoodList;
        this.foodEnList = foodEnList;
        this.monoMartList = monoMartList;
        this.bestList = bestList;
    }

}
