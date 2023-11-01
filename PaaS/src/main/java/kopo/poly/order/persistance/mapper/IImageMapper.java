package kopo.poly.order.persistance.mapper;

import kopo.poly.order.dto.ImageDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IImageMapper {
    int insertImageUrlCheckY(ImageDTO pDTO) throws Exception;
    int insertImageUrlCheckN(ImageDTO pDTO) throws Exception;
}
