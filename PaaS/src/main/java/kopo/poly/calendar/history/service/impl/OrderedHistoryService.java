package kopo.poly.calendar.history.service.impl;

import kopo.poly.calendar.history.dto.CalendarEventDTO;
import kopo.poly.calendar.history.dto.OrderedDTO;
import kopo.poly.calendar.history.dto.OrderedHistoryDTO;
import kopo.poly.calendar.history.persistance.mapper.IOrderedHistoryMapper;
import kopo.poly.calendar.history.service.IOrderedHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderedHistoryService implements IOrderedHistoryService {

    private final IOrderedHistoryMapper orderedHistoryMapper;

    /**
     * 유저정보 기반으로 캘린더에 띄워줄 OrderedHistoryDTO 의 List 만들기
     * 이거를 CalendarEventDTO 로 만드는건 어디서 할지 고민중
     *
     * @param pDTO 유저정보가 담겨있음 (userId)
     * @return userId 기반으로 모든 발주 가져오기 (group by 함수 활용)
     */
    @Override
    public List<CalendarEventDTO> getUserCalendarEventList(OrderedHistoryDTO pDTO) {
        log.info(this.getClass().getName() + ".getOrderedHistoryDTOList() START!!!!!!!!!!");
        log.info("캘린더에서 띄워줄 데이터 가져올거임");
        log.info("pDTO : " + pDTO);


        List<OrderedHistoryDTO> historyDTOList;
        historyDTOList = Optional.ofNullable(
                orderedHistoryMapper.getUserOrderedHistoryDTOList(pDTO)
        ).orElseGet(ArrayList::new);


        List<CalendarEventDTO> rList = null;

        historyDTOList.stream().forEach(dto -> {
            log.info(dto.toString());
        });

        return rList;
    }

    /**
     * @param pDTO 유저정보와 ocrDate 가 담겨있음 이거로 조회해야됨
     *             (userId, ocrDate)
     * @return userId 와 ocrDate 를 기반으로 해당 일의 모든 발주 가져오기
     */
    @Override
    public List<OrderedDTO> getUserOrderedList(OrderedHistoryDTO pDTO) {
        log.info(this.getClass().getName() + ".getOrderedHistoryDTOList() START!!!!!!!!!!");
        log.info("/calendar/detail 에서 띄워줄 데이터 가져올거임");
        log.info("pDTO : " + pDTO);


        List<OrderedDTO> rList;

        rList = Optional.ofNullable(
                orderedHistoryMapper.getUserOrderedList(pDTO)
        ).orElseGet(ArrayList::new);
        return rList;
    }
}
