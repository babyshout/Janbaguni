package kopo.poly.order.history.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequestMapping("/order/history")
@RequiredArgsConstructor
@Controller
public class OrderedHistoryController {


    @GetMapping("calendar")
    public String getCalendar() {
        log.info(this.getClass().getName() + ".getCalendar() START!!!!!!!!!!!!!");

        return "/order/calendar/ordered-history-calendar";
    }

    /**
     * 일단 test 메서드 호출하고, 이거 캘린더에서 잘 띄워지는지 확인
     * 이거 하기전에 해야될거 -> DTO 확인,,, 어떤거 들고올건지
     * 1. 일단 fullcalendarJS 로 이벤트 잘 넘어가는지 확인하기
     * 2. fullcalendar JS event 들 구분해줄 id (PK) 어떻게 구분할건지
     * 3. 같은 일자에 2개 발주내역이 있으면 어떻게 처리할건지
     * 4. 테이블 보고 sql문 어떻게 짤지, DTO 어떻게 만들지
     * @param httpSession
     * @return
     */
    @ResponseBody
    @GetMapping("getCalendarTestData")
    public Map<String, String> getCalendarTestData(
            HttpSession httpSession,
            HttpServletRequest request,
            Model model
    ) throws Exception {
        Map<String, String> rDTO = new HashMap<>();


        return rDTO;
    }
    @ResponseBody
    @GetMapping("test")
    public String getTest() {
        log.info(this.getClass().getName() + ".getTest() START!!!!!!!!!!!!!");
        return "test";
    }

}
