package kopo.poly.user.controller;

import kopo.poly.user.dto.UserInfoDTO;
import kopo.poly.user.service.IUserInfoService;
import kopo.poly.user.util.CmmUtil;
import kopo.poly.user.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * user 아이디, 패스워드 찾아주는 클래스임.
 * 폼 하나만 만들어놓고, 해당 폼에서 모든 동작이 끝났으면 함.
 * 
 * 기본적인 form 불러주는 method,
 * 각 form 에서 해주는 동작들,,,
 * (id 찾기, password 찾기)
 * 
 * id 찾기 -> 그냥 이름, 이메일 던지면, 아래 결과창에 결과 바로뜨게
 *
 * password 찾기 -> 그냥 이름, 이메일, id 던지는걸로 바로 돌아가게 해도 되나???
 * random 비밀번호 날려줘서,,, 거기로 받아오게,,,
 */
@Slf4j
@Controller
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchUserController {
    private final IUserInfoService userInfoService;

    @GetMapping("form")
    public String searchForm() {
        log.info(this.getClass().getName() + ".searchForm() START!!!!!!!!!!!!");
        return "/user/search/search-form";
    }

    /**
     * 유효한 접근이면 아이디 알려줄거임
     *
     * @param session 일단 넣어봤음
     * @param request 없으면 파라미터 못뽑아옴
     * @return "msg" : 띄워줄 메시지
     * "userId" : 유저 아이디
     * "resultInteger" : 1 아니면 실패했다고 alert 띄우기
     * @throws Exception 그냥 Exception 으로 오류핸들링 다하더라,,, 나중에 고치면 좋을듯
     */
    @ResponseBody
    @PostMapping(value = "userIdProcedure")
    public Map<String, String> searchUserIdProcedure(
            HttpSession session,
            HttpServletRequest request
    ) throws Exception {
        log.info(this.getClass().getName() + "./searchUserIdProcedure Start!");

        String userName = CmmUtil.nvl(request.getParameter("userName")); // 이름
        String email = CmmUtil.nvl(request.getParameter("email")); // 이메일

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserName(userName);
        pDTO.setEmail(EncryptUtil.encAES128CBC(email));

        log.info("pDTO : " + pDTO.toString());

        UserInfoDTO rDTO = Optional.ofNullable(
                userInfoService.searchUserIdOrPasswordProc(pDTO)
        ).orElseGet(UserInfoDTO::new);

        log.info("rDTO.getUserId() : " + rDTO.getUserId());

        log.info(this.getClass().getName() + "./searchUserIdProc End!");

        Map<String, String> resultMap = new HashMap<>();

        //msg userId resultInteger

        if (!isHavingValidId(rDTO)) {

            resultMap.put("msg", "아이디가 없습니다!!");
            resultMap.put("userId", CmmUtil.nvl(rDTO.getUserId()));
            resultMap.put("resultInteger", "0");
        } else {
            resultMap.put("msg", "아이디를 찾았습니다!!");
            resultMap.put("userId", CmmUtil.nvl(rDTO.getUserId()));
            resultMap.put("resultInteger", "1");
        }

        return resultMap;
    }

    private boolean isHavingValidId(UserInfoDTO userInfo) {
        return (CmmUtil.nvl(userInfo.getUserId()).length() > 0);
    }

    @PostMapping(value = "/searchUserIdProc")
    public String searchUserIdProc(HttpServletRequest request, ModelMap model) throws Exception {
        log.info(this.getClass().getName() + "./searchUserIdProc Start!");

        String userName = CmmUtil.nvl(request.getParameter("userName")); // 이름
        String email = CmmUtil.nvl(request.getParameter("email")); // 이메일

        log.info("userName : " + userName);
        log.info("email : " + email);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserName(userName);
        pDTO.setEmail(EncryptUtil.encAES128CBC(email));

        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.searchUserIdOrPasswordProc(pDTO)).orElseGet(UserInfoDTO::new);

        model.addAttribute("rDTO", rDTO);

        log.info("rDTO.getUserId() : " + rDTO.getUserId());

        log.info(this.getClass().getName() + "./searchUserIdProc End!");

        return "/user/bingguxi/find/find-id-result";
    }

    /**
     * 유효한 접근이면 새 패스워드 던져줄거임
     * "msg" : 띄워줄 메시지
     * "url" : location.href 로 이동할 위치 (대부분 로그인)
     * "resultInteger" : 1 아니면 그냥 가만히 있어야 될듯?
     *
     * @param session
     * @param request
     * @return
     * @throws Exception
     */
    @ResponseBody
    @PostMapping(value = "newPasswordProcedure")
    public Map<String, String> newPasswordProcedure(
            HttpSession session,
            HttpServletRequest request
    ) throws Exception {
        log.info(this.getClass().getName() + ".newPasswordProc START!!!!!!!!!!!!!!");

        Map<String, String> resultMap = new HashMap<>();

        int resultInteger;
        String msg;
        String url;

        log.info(this.getClass().getName() + ".newPasswordProc END!!!!!!!!!!!!!!");
        return resultMap;
    }

    /**
     * 유효한 접근이면 새 패스워드 던져줄거임
     * "msg" : 띄워줄 메시지
     * "url" : location.href 로 이동할 위치 (대부분 로그인)
     * "resultInteger" : 1 아니면 그냥 가만히 있어야 될듯?
     *
     * @param session
     * @param request
     * @return
     * @throws Exception
     */
    @ResponseBody
    @PostMapping(value = "newPasswordProc")
    public Map<String, String> newPasswordProc(
            HttpSession session,
            HttpServletRequest request
    ) throws Exception {
        log.info(this.getClass().getName() + ".newPasswordProc START!!!!!!!!!!!!!!");

        Map<String, String> resultMap = new HashMap<>();

        int resultInteger;
//        String msg;
//        String url;
        // 입력받은 값을 변수에 저장하기
        String userId = CmmUtil.nvl(request.getParameter("userId")); // 아이디
        String userName = CmmUtil.nvl(request.getParameter("userName")); // 이름
        String email = CmmUtil.nvl(request.getParameter("email")); // 이메일

        log.info("userId : " + userId);
        log.info("userName : " + userName);
        log.info("email : " + email);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserId(userId);
        pDTO.setUserName(userName);
        pDTO.setEmail(EncryptUtil.encAES128CBC(email));

        String msg = "";
        String url = "";

        int res = userInfoService.pwCode(pDTO);

        if (res == 1) {
            msg = "가입하신 메일로 임시 비밀번호를 전송하였습니다.";
            url = "/find/passwd/result";
            resultInteger = res;
        } else {
            msg = "이메일이 등록되어 있지 않습니다. 다시 한번 확인해주세요.";
            url = "/find/passwd";
            resultInteger = res;
        }

//        model.addAttribute("msg", msg);
//        model.addAttribute("url", url);

        resultMap.put("msg", msg);
        resultMap.put("url", url);
        resultMap.put("resultInteger", String.valueOf(resultInteger));
        log.info("String.valueOf(resultInteger) : " + String.valueOf(resultInteger));

        log.info(getClass().getName() + "./newPassword End!");

        log.info(this.getClass().getName() + ".newPasswordProc END!!!!!!!!!!!!!!");
        return resultMap;
    }
}
