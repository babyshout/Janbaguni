package kopo.poly.order.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class CrawlingComposite {
    private List<List<ProductCrawlingDTO>> aceList;
    private List<List<ProductCrawlingDTO>> goodFoodList;
    private List<List<ProductCrawlingDTO>> babyLeafList;

    public CrawlingComposite(List<List<ProductCrawlingDTO>> aceList, List<List<ProductCrawlingDTO>> goodFoodList, List<List<ProductCrawlingDTO>> babyLeafList){
        this.aceList = aceList;
        this.babyLeafList = babyLeafList;
        this.goodFoodList = goodFoodList;
    }


}
