package kopo.poly.order.history.persistance.mapper;

import kopo.poly.order.history.dto.OrderedHistoryDTO;

import java.util.List;

public interface IOrderedHistoryMapper {

    List<OrderedHistoryDTO> getUserOrderedHistory();
}
