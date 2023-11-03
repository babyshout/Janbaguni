package kopo.poly.order.persistance.mapper;

import kopo.poly.order.dto.OcrDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IImageMapper {
    int insertImageUrlCheckY(OcrDTO pDTO) throws Exception;
    int insertImageUrlCheckN(OcrDTO pDTO) throws Exception;
}
