package kopo.poly.order.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Getter
@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonDeserialize
public class SearchCrawlingComposite {
    private List<ProductCrawlingDTO> searchAce;
    private List<ProductCrawlingDTO> searchGoodFood;
    private List<ProductCrawlingDTO> searchBabyLeaf;
    private List<ProductCrawlingDTO> searchFoodEn;
    private List<ProductCrawlingDTO> searchMonoMart;

    public SearchCrawlingComposite(){}

    public SearchCrawlingComposite(List<ProductCrawlingDTO> searchAce, List<ProductCrawlingDTO> searchGoodFood,
                             List<ProductCrawlingDTO> searchBabyLeaf, List<ProductCrawlingDTO> searchFoodEn,
                             List<ProductCrawlingDTO> searchMonoMart){
        this.searchAce = searchAce;
        this.searchGoodFood = searchGoodFood;
        this.searchBabyLeaf = searchBabyLeaf;
        this.searchFoodEn = searchFoodEn;
        this.searchMonoMart = searchMonoMart;
    }
}
