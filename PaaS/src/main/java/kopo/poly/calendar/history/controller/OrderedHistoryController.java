package kopo.poly.calendar.history.controller;

import kopo.poly.calendar.history.dto.CalendarEventDTO;
import kopo.poly.calendar.history.dto.OrderedHistoryByDayDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequestMapping("/order/history")
@RequiredArgsConstructor
@Controller
public class OrderedHistoryController {


    @GetMapping("calendar")
    public String getCalendar() {
        log.info(this.getClass().getName() + ".getCalendar() START!!!!!!!!!!!!!");

        return "/calendar/ordered-history-calendar";
    }

    @GetMapping("detail")
    public String getDetail(
            Model model,
            HttpServletRequest request,
            HttpSession session,
//            @RequestBody CalendarEventDTO calendarEventDTO
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ocrDate
    ) throws Exception {
        log.info(this.getClass().getName() + ".getDetail() START!!!!!!!!!!!!!");
        log.info("ocrDate : " + ocrDate.toString());
        if (session.isNew() || session.getAttribute("SS_USER_ID").equals("")) {
            log.warn("session is new!!!!");
            return "redirect:/login/login-form";
        }
//        log.info("calendarEventDTO : " + calendarEventDTO.toString());
//        log.info("paramLocalDate : " + paramLocalDate.toString());
//        request.getParameter("")

        return "/calendar/ordered-history-detail";
    }

    /**
     * 일단 test 메서드 호출하고, 이거 캘린더에서 잘 띄워지는지 확인
     * 이거 하기전에 해야될거 -> DTO 확인,,, 어떤거 들고올건지
     * 1. 일단 fullcalendarJS 로 이벤트 잘 넘어가는지 확인하기
     * 2. fullcalendar JS event 들 구분해줄 id (PK) 어떻게 구분할건지
     * 3. 같은 일자에 2개 발주내역이 있으면 어떻게 처리할건지
     * 4. 테이블 보고 sql문 어떻게 짤지, DTO 어떻게 만들지
     *
     * @param httpSession
     * @return
     */
    @ResponseBody
    @GetMapping("getCalendarTestData")
    public List<CalendarEventDTO> getCalendarTestData(
            HttpSession httpSession,
            HttpServletRequest request,
            Model model
    ) throws Exception {
//        Map<String, String> rDTO = new HashMap<>();

        List<CalendarEventDTO> rList = new ArrayList<>();

        CalendarEventDTO dto = new CalendarEventDTO();
        OrderedHistoryByDayDTO orderedHistoryByDayDTO = new OrderedHistoryByDayDTO();
        String url;

        orderedHistoryByDayDTO.setUserId("USER_ID");
        orderedHistoryByDayDTO.setPrice(Integer.parseInt("100000"));
        orderedHistoryByDayDTO.setOcrDate(LocalDate.now());
        dto.setOrderedHistoryByDayDTO(orderedHistoryByDayDTO);
        dto.setStart(orderedHistoryByDayDTO.getOcrDate());
        dto.setTitle(dto.getStart().toString() + "의 타이틀");
        url = "/order/history/detail?ocrDate=" + orderedHistoryByDayDTO.getOcrDate();
        dto.setUrl(url);

        log.info("dto1 : " + dto.toString());

        rList.add(dto);


        dto = new CalendarEventDTO();
        orderedHistoryByDayDTO = new OrderedHistoryByDayDTO();

        orderedHistoryByDayDTO.setUserId("USER_ID");
        orderedHistoryByDayDTO.setPrice(Integer.parseInt("200000"));
        orderedHistoryByDayDTO.setOcrDate(LocalDate.of(2023, 12, 25));
        dto.setOrderedHistoryByDayDTO(orderedHistoryByDayDTO);
        dto.setStart(orderedHistoryByDayDTO.getOcrDate());
        dto.setTitle(dto.getStart().toString() + "의 타이틀");
        url = "/order/history/detail?ocrDate" + orderedHistoryByDayDTO.getOcrDate();
        dto.setUrl(url);

        log.info("dto2 : " + dto.toString());


        rList.add(dto);


        return rList;
    }

    @ResponseBody
    @GetMapping("test")
    public String getTest() {
        log.info(this.getClass().getName() + ".getTest() START!!!!!!!!!!!!!");
        return "test";
    }

}
