package kopo.poly.calendar.history.controller;

import kopo.poly.calendar.history.dto.CalendarEventDTO;
import kopo.poly.calendar.history.dto.OrderedDTO;
import kopo.poly.calendar.history.dto.OrderedHistoryByDayDTO;
import kopo.poly.calendar.history.service.IOrderedHistoryService;
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
import java.util.Optional;

@Slf4j
@RequestMapping("/order/history")
@RequiredArgsConstructor
@Controller
public class OrderedHistoryController {
    
    private final IOrderedHistoryService orderedHistoryService;


    @GetMapping("calendar")
    public String getCalendar() {
        log.info(this.getClass().getName() + ".getCalendar() START!!!!!!!!!!!!!");

        return "/calendar/ordered-history-calendar";
    }

//    @ResponseBody
    @GetMapping("detail")
    public String getDetail(
            Model model,
            HttpServletRequest request,
            HttpSession session,
//            @RequestBody CalendarEventDTO calendarEventDTO
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ocrDate
    ) throws Exception {
        log.info(this.getClass().getName() + ".getDetail() START!!!!!!!!!!!!!");
        log.info("ocrDateLocalDate : " + ocrDate.toString());
        if (session.isNew() || session.getAttribute("SS_USER_ID").equals("")) {
            log.warn("session is new!!!!");
//            return "redirect:/login/login-form";
        }
//        log.info("calendarEventDTO : " + calendarEventDTO.toString());
//        log.info("paramLocalDate : " + paramLocalDate.toString());
//        request.getParameter("")
        String userId = (String) session.getAttribute("SS_USER_ID");

        OrderedHistoryByDayDTO pDTO = new OrderedHistoryByDayDTO();
        pDTO.setUserId(userId);
        pDTO.setOcrDateLocalDate(ocrDate);
        List<OrderedDTO> rList = Optional.ofNullable(
                orderedHistoryService.getUserOrderedList(pDTO)
        ).orElseGet(ArrayList::new);

        log.info("rList in contoller : " + rList);

        model.addAttribute("rList", rList);



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
        log.info(this.getClass().getName() + ".getCalendarTestData() START!!!!");
        log.info("유저의 calendarData test 를 가져오는 메서드");

        List<CalendarEventDTO> rList = new ArrayList<>();

        CalendarEventDTO dto = new CalendarEventDTO();
        OrderedHistoryByDayDTO orderedHistoryByDayDTO = new OrderedHistoryByDayDTO();
        String url;

        orderedHistoryByDayDTO.setUserId("USER_ID");
        orderedHistoryByDayDTO.setPriceSum(Integer.parseInt("100000"));
        orderedHistoryByDayDTO.setOcrDateLocalDate(LocalDate.now());
        dto.setOrderedHistoryByDayDTO(orderedHistoryByDayDTO);
        dto.setStart(orderedHistoryByDayDTO.getOcrDateLocalDate());
        dto.setTitle(dto.getStart().toString() + "의 타이틀 가격은" +
                orderedHistoryByDayDTO.getPriceSum());
        url = "/order/history/detail?ocrDateLocalDate=" + orderedHistoryByDayDTO.getOcrDateLocalDate();
        dto.setUrl(url);

        log.info("dto1 : " + dto.toString());

        rList.add(dto);


        dto = new CalendarEventDTO();
        orderedHistoryByDayDTO = new OrderedHistoryByDayDTO();

        orderedHistoryByDayDTO.setUserId("USER_ID");
        orderedHistoryByDayDTO.setPriceSum(Integer.parseInt("200000"));
        orderedHistoryByDayDTO.setOcrDateLocalDate(LocalDate.of(2023, 12, 25));
        dto.setOrderedHistoryByDayDTO(orderedHistoryByDayDTO);
        dto.setStart(orderedHistoryByDayDTO.getOcrDateLocalDate());
        dto.setTitle(dto.getStart().toString() + "의 타이틀 가격은" +
                orderedHistoryByDayDTO.getPriceSum());
        url = "/order/history/detail?ocrDateLocalDate" + orderedHistoryByDayDTO.getOcrDateLocalDate();
        dto.setUrl(url);

        log.info("dto2 : " + dto.toString());


        rList.add(dto);


        return rList;
    }


    /**
     * 
     * @param session
     * @return
     */
    @ResponseBody
    @GetMapping("/getCalendarData")
    public List<CalendarEventDTO> getCalendarData(
        HttpSession session
        
    ) {
        log.info(this.getClass().getName() + ".getCalendarData() START!!");
        log.info("유저의 calendar 이벤트 가져오는 메서드");
        if (session.isNew() || session.getAttribute("SS_USER_ID").equals("")) {
            log.warn("session is new!!!!");
            session.setAttribute("SS_USER_ID", "user01");

//            return "redirect:/login/login-form";
        }


        List<CalendarEventDTO> rList;


        
        // 코딩 규칙을 모두가 지키면 그것 자체로 생산성이 올라가는구나
        OrderedHistoryByDayDTO pDTO = new OrderedHistoryByDayDTO();
        pDTO.setUserId((String) session.getAttribute("SS_USER_ID"));

        rList = orderedHistoryService.getUserCalendarEventList(pDTO);

        log.info("rList : (controller) " + rList.toString());
        
        
        
        return rList;
    }

    @ResponseBody
    @GetMapping("test")
    public String getTest() {
        log.info(this.getClass().getName() + ".getTest() START!!!!!!!!!!!!!");
        return "test";
    }

}
