package kopo.poly.order.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class CrawlingComposite {
    private List<List<ProductCrawlingDTO>> aceList;
    private List<List<ProductCrawlingDTO>> goodFoodList;
    private List<List<ProductCrawlingDTO>> babyLeafList;

    private List<List<ProductCrawlingDTO>> foodEnList;

    private List<List<ProductCrawlingDTO>> monoMartList;



    public CrawlingComposite(List<List<ProductCrawlingDTO>> aceList, List<List<ProductCrawlingDTO>> goodFoodList,
                             List<List<ProductCrawlingDTO>> babyLeafList, List<List<ProductCrawlingDTO>> foodEnList,
                             List<List<ProductCrawlingDTO>> monoMartList){
        this.aceList = aceList;
        this.babyLeafList = babyLeafList;
        this.goodFoodList = goodFoodList;
        this.foodEnList = foodEnList;
        this.monoMartList = monoMartList;
    }

}
