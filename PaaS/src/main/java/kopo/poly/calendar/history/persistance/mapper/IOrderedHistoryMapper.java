package kopo.poly.calendar.history.persistance.mapper;

import kopo.poly.calendar.history.dto.OrderedDTO;
import kopo.poly.calendar.history.dto.OrderedHistoryByDayDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IOrderedHistoryMapper {

    List<OrderedHistoryByDayDTO> getUserOrderedHistoryDTOList(OrderedHistoryByDayDTO pDTO);
    List<OrderedDTO> getUserOrderedList(OrderedHistoryByDayDTO pDTO);
}
