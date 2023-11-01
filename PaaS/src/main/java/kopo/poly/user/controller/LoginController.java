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

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/login")
public class LoginController {

    private final IUserInfoService userInfoService;

    /*
     * 로그인을 위한 입력 화면으로 이동
     * */
    @GetMapping("/login-form")
    public String userLogin() {
        log.info(this.getClass().getName() + "./login start!");
        log.info(this.getClass().getName() + "./login end!");

        return "/user/sign-in_sign-up";
    }

    /*
     * 로그인 처리 및 결과 알려주는 화면으로 이동
     * */
    @PostMapping(value = "/loginProc")
    @ResponseBody // JSON 형식으로 응답을 보내기 위해 필요한 어노테이션
    public Map<String, Object> loginProc(HttpServletRequest request, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        int res = 0; // 로그인 처리 결과를 저장할 변수
        String msg = "";
        String url = "/main";

        UserInfoDTO pDTO = null;

        try {
            String userId = CmmUtil.nvl(request.getParameter("userId")); // 아이디 /* 키를 넘겨주면서 값을 가져올 수 있다! userId라는 키로 아이디 값을 가져올 수 있음*/
            String password = CmmUtil.nvl(request.getParameter("password")); // 비밀번호

            log.info("userId : " + userId);
            log.info("password : " + password);

            // 웹(회원정보 입력화면)에서 받는 정보를 저장할 변수를 메모리에 올리기
            pDTO = new UserInfoDTO();

            pDTO.setUserId(userId);

            // 비밀번호는 절대로 복호화되지 않도록 해시 알고리즘으로 암호화함
            pDTO.setPassword(EncryptUtil.encHashSHA256(password));

            // 로그인을 위해 아이디와 비밀번호가 일치하는지 확인하기 위한 userInfoService 호출하기
            UserInfoDTO rDTO = userInfoService.getLogin(pDTO);

            if (CmmUtil.nvl(rDTO.getUserId()).length() > 0) { // 로그인 성공

                res = 1;

                session.setAttribute("SS_USER_ID", userId);
                session.setAttribute("SS_USER_NAME", CmmUtil.nvl(rDTO.getUserName()));

                msg = "로그인 성공! " + rDTO.getUserName() + "님 환영합니다.";
                url = "/main";

            } else {

                msg = "아이디와 비밀번호가 올바르지 않습니다.";
            }
        } catch (Exception e) {

            msg = "시스템 문제로 로그인이 실패했습니다.";
            log.error(e.toString());
            e.printStackTrace();

        }
        log.info("result : " + res);
        log.info("msg : " + msg);
        log.info("url : " + url);

        response.put("result", res);
        response.put("msg", msg);
        response.put("url", url);
        return response;
    }

    @GetMapping(value = "/find")
    public String loginFind() {
        log.info(this.getClass().getName() + "./find start!");

        return "/user/bingguxi/find/find";
    }

    // 아이디 찾기
    @GetMapping(value = "/find/id")
    public String loginFindId() {
        log.info(this.getClass().getName() + "./find/id start!");

        log.info(this.getClass().getName() + "./find/id end!");

        return "/user/bingguxi/find/find-id";
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

    // 비밀번호 찾기
    @GetMapping(value = "/find/passwd")
    public String loginFindPasswd(HttpSession session) {
        log.info(this.getClass().getName() + "./find/passwd start!");

        // 강제 URL 입력 등 오는 경우가 있어 세션 삭제
        // 비밀번호 재생성하는 화면은 보안을 위해 생성한 NEW_PASSWORD 세션 삭제
        session.setAttribute("NEW_PASSWORD", "");
        session.removeAttribute("NEW_PASSWORD");

        log.info(this.getClass().getName() + "./find/passwd end!");

        return "/user/bingguxi/find/find-passwd";
    }

    // 비밀번호 찾기 전 아이디, 이름, 이메일 입력받고 확인하기
    /*@PostMapping(value = "/searchPasswordProc")
    public String searchPasswordProc(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".user/bingguxi/find/search");

        *//* 웹에서 입력받은 값을 받아오기 *//*
        String userId = CmmUtil.nvl(request.getParameter("userId")); // 아이디
        String userName = CmmUtil.nvl(request.getParameter("userName")); // 이름
        String email = CmmUtil.nvl(request.getParameter("email")); // 이메일

        *//* 받아온 값을 로그 찍어서 확인해보기 *//*
        log.info("userId : " + userId);
        log.info("userName : " + userName);
        log.info("email : " + email);

        *//* 받아온 값을 DTO에 저장하기 *//*
        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserId(userId);
        pDTO.setUserName(userName);
        pDTO.setEmail(EncryptUtil.encAES128CBC(email));

        // 비밀번호 찾기 가능한지 확인하기
        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.searchUserIdOrPasswordProc(pDTO)).orElseGet(UserInfoDTO::new);

        model.addAttribute("rDTO", rDTO);

        // 비밀번호 재생성하는 화면은 보안을 위해 반드시 NEW_PASSWORD 세션이 존재해야 접속 가능하도록 구현
        // userId 값을 넣은 이유는 비밀번호 재설정하는 newPasswordProc 함수에서 사용하기 위함
        session.setAttribute("NEW_PASSWORD", userId);

        log.info(this.getClass().getName() + "./searchPasswordProc End!");

        return "user/bingguxi/find/find-passwd-new";

    }*/

    // 비밀번호 재설정하기
    /*@PostMapping(value = "/newPasswordProc")
    public String newPasswordProc(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + "./newPasswordProc Start!");

        String msg = ""; // 웹에 보여줄 메시지

        // 정상적인 접근인지 체크
        String newPassword = CmmUtil.nvl((String) session.getAttribute("NEW_PASSWORD"));

        if (newPassword.length() > 0) { // 정상 접근

            String password = CmmUtil.nvl(request.getParameter("password")); // 신규 비밀번호

            log.info("password : " + password);

            UserInfoDTO pDTO = new UserInfoDTO();
            pDTO.setUserId(newPassword);
            pDTO.setPassword(EncryptUtil.encHashSHA256(password));

            userInfoService.newPasswordProc(pDTO);

            // 비밀번호 재생성하는 화면은 보안을 위해 생성한 NEW_PASSWORD 세션 삭제
            session.setAttribute("NEW_PASSWORD", "");
            session.removeAttribute("NEW_PASSWORD");

            msg = "비밀번호가 재설정되었습니다.";

        } else { // 비정상 접근
            msg = "비정상 접근입니다.";
        }

        model.addAttribute("msg", msg);

        log.info(this.getClass().getName() + "./newPasswordProc End!");

        return "user/bingguxi/find/find-passwd-new-result";
    }
     */

    @GetMapping(value = "/main")
    public String Main() {
        log.info(this.getClass().getName() + "./main start!");

        log.info(this.getClass().getName() + "./main end!");

        return "/user/index";
    }

    /**
     * 이메일 일치 확인 후에 임시 비밀번호 전송
     */
    @PostMapping(value = "/newPasswordProc")
    public String pwCode(HttpServletRequest request, ModelMap model) throws Exception {

        log.info(getClass().getName() + "./newPassword Start!");

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
        } else {
            msg = "이메일이 등록되어 있지 않습니다. 다시 한번 확인해주세요.";
            url = "/find/passwd";
        }

        model.addAttribute("msg", msg);
        model.addAttribute("url", url);

        log.info(getClass().getName() + "./newPassword End!");

        return "/redirect";
    }

    @GetMapping(value = "/find/passwd/result")
    public String findPasswdResult() {

        log.info(this.getClass().getName() + "./find/passwd/result start!");

        log.info(this.getClass().getName() + "./find/passwd/result end!");

        return "/user/bingguxi/find/find-passwd-result";
    }

}
