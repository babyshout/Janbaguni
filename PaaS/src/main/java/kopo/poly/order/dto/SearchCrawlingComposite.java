package kopo.poly.order.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class SearchCrawlingComposite {
    private List<ProductCrawlingDTO> searchAce;
    private List<ProductCrawlingDTO> searchGoodFood;
    private List<ProductCrawlingDTO> searchBabyLeaf;
    private List<ProductCrawlingDTO> searchFoodEn;
    private List<ProductCrawlingDTO> searchMonoMart;

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
