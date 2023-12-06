package kopo.poly.calendar.history.persistance.mapper;

import kopo.poly.calendar.history.dto.OrderedDTO;
import kopo.poly.calendar.history.dto.OrderedHistoryDTO;

import java.util.List;

public interface IOrderedHistoryMapper {

    List<OrderedHistoryDTO> getUserOrderedHistoryDTOList(OrderedHistoryDTO pDTO);
    List<OrderedDTO> getUserOrderedList(OrderedHistoryDTO pDTO);
}
