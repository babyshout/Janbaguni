package kopo.poly.order.service.impl;

import kopo.poly.order.dto.ProductCrawlingDTO;
import kopo.poly.order.service.ICrawlingService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CrawlingService implements ICrawlingService {

    /**
     * @param url      호출하는 메소드에서 키워드를 포함한 url로 재정의
     * @param selector 상품 정보를 담고있는 부모 요소의 id 혹은 class
     * @param price    가격 정보를 담고있는 요소의 id 혹은 class
     * @param name     상품명을 담고있는 요소의 id 혹은 class
     * @param shop     발주처
     * @return
     * @throws IOException
     */
    /**
     * @param url      호출하는 메소드에서 키워드를 포함한 url로 재정의
     * @param selector 상품 정보를 담고있는 부모 요소의 id 혹은 class
     * @param price    가격 정보를 담고있는 요소의 id 혹은 class
     * @param name     상품명을 담고있는 요소의 id 혹은 class
     * @param shop     발주처
     * @return
     * @throws IOException
     */
    private List<ProductCrawlingDTO> getCrawlingData(String url, String selector, String price, String name, String shop, String lingUrl, String getUrl) throws IOException {
        log.info("getCrawliingData Start! ");
        List<ProductCrawlingDTO> productList = new ArrayList<>();

        Document document = Jsoup.connect(url).get();
        Elements contents = document.select(selector);

        for (Element content : contents) {

            ProductCrawlingDTO pDTO = new ProductCrawlingDTO();

            pDTO.setPrice(content.select(price).first().text());
            pDTO.setName(content.select(name).first().text());
            pDTO.setShop(shop);
            Element tmp = content.select(getUrl).first();
            String resultUrl = lingUrl + tmp.attr("href");
            pDTO.setLinkUrl(resultUrl);
            productList.add(pDTO);

        }
        if(!productList.isEmpty()){
            return productList;
        }else{
            return null;
        }

    }

    /**
     * @param keyword 상품 검색에 사용될 키워드
     * @return
     * @throws IOException
     */
    @Override
    public List<ProductCrawlingDTO> getAceData(String keyword) throws IOException {
        String cUrl = "https://m.acemall.asia/shop/search.php?search=&keyword=" + keyword + "&y=0";
        List<ProductCrawlingDTO> productCrawlingDTOList = new ArrayList<>();
        productCrawlingDTOList = getCrawlingData(cUrl, ".info", ".price strong", ".info .name a", "에이스 식자재", "https://www.acemall.asia", ".name a");
        return productCrawlingDTOList;
    }

    /**
     * @param keyword 상품 검색에 사용될 키워드
     * @return
     * @throws IOException
     */
    @Override
    public List<ProductCrawlingDTO> getGoodFood(String keyword) throws IOException {
        String cUrl = "https://m.goodfoodmall.co.kr/goods/goods_search.php?keyword=" + keyword;
        return getCrawlingData(cUrl, ".goods_list_info", ".c_price", ".prd_name", "굿푸드몰","https://m.goodfoodmall.co.kr/goods/", "a");
    }

    @Override
    public List<ProductCrawlingDTO> getBabyleaf(String keyword) throws IOException {
        String cUrl = "http://www.babyleaf.co.kr/product/search.html?banner_action=&keyword=" + keyword;
        return getCrawlingData(cUrl, ".description", "li[rel='판매가'] span:nth-child(2)", ".name a span:nth-child(2)", "짱구몰","http://www.babyleaf.co.kr",".name a");
    }

}
