package kopo.poly.order.service;

import kopo.poly.order.dto.ProductCrawlingDTO;

import java.io.IOException;
import java.util.List;

public interface ICrawlingService {
    List<ProductCrawlingDTO> getAceData(String keyword) throws IOException; //에이스 식자재

    List<ProductCrawlingDTO> getGoodFood(String keyword) throws IOException; // 굿푸드몰

    List<ProductCrawlingDTO> getBabyleaf(String keyword) throws IOException; // 짱구몰

}
