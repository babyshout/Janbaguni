package kopo.poly.user.controller;

import kopo.poly.user.dto.UserInfoDTO;
import kopo.poly.user.enumx.SessionEnum;
import kopo.poly.user.service.IUserInfoService;
import kopo.poly.user.util.CmmUtil;
import kopo.poly.user.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/profile")
public class MemberController {
    private final IUserInfoService userInfoService;

    @GetMapping(value = "info")
    public String profileInfo(
            HttpSession session,
            HttpServletRequest servletRequest,
            Model model
    ) throws Exception {
        // 유효한 세션아이디가 아니면 /login/login-form 으로 redirect
        if (!isValidSessionUserId(session)) {
//            session.setAttribute(SessionEnum.USER_ID.STRING, "USER01");
            return "redirect:/login/login-form";
        }
        log.info(this.getClass().getName() + ".getMemberInfo() START!!!!!!!!!!!!!!");

        UserInfoDTO pDTO = new UserInfoDTO();
        String userId = (String) session.getAttribute(SessionEnum.USER_ID.STRING);

        pDTO.setUserId(userId);

        log.info("pDTO : " + pDTO.toString());

        UserInfoDTO rDTO = userInfoService.getUserInfo(pDTO);
        rDTO.setEmail(
                EncryptUtil.decAES128CBC(CmmUtil.nvl(rDTO.getEmail()))
        );

        log.info("rDTO : " + rDTO.toString());

        model.addAttribute("rDTO", rDTO);

        log.info(this.getClass().getName() + ".getMemberInfo() END!!!!!!!!!!!!!!");


        if (rDTO.getUserType().equals("biz")) {
            return "/user/profile/info/profile-biz-info";
        } else {
            return "/user/profile/info/profile-newbiz-info";
        }
    }

    /**
     * 컨트롤러에서 session 받아서 유효한 UserId 인지 검사
     * @param session 이 메서드를 호출한 컨트롤러에서 받아옴
     * @return null 이나 "" 이 아니면 true
     */
    private boolean isValidSessionUserId(HttpSession session){
        return !(session.isNew() || ((String) session.getAttribute(SessionEnum.USER_ID.STRING)).equals(""));
    }

    @GetMapping(value = "check-password-form")
    public String checkPasswordForm(
            HttpSession session,
            HttpServletRequest httpServletRequest
    ) {
        log.info(this.getClass().getName() + "\tcheck-password-form CALLED!!!!!!!!!");
        return "/user/profile/edit/profile-password-check";
    }

    @GetMapping(value = "check-password-proc")
    public String checkPassword(
            HttpSession session,
            HttpServletRequest request,
            @RequestParam(name = "password") String password
    ) {
        log.info(this.getClass().getName() + ".checkPassword() START!!!!!!!!!!");

        return "/user/profile/edit/profile-password-check";
//        return "redirect:/profile/check-password";

//        UserInfoDTO rDTO;
//
//        log.info(this.getClass().getName() + ".checkPassword() END!!!!!!!!!!");
//        if ((rDTO.getUserType().equals("biz"))) {
//            return "/user/profile/edit/profie-biz-edit";
//        }

    }
}
