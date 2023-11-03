package kopo.poly.user.controller;

import kopo.poly.user.dto.UserInfoDTO;
import kopo.poly.user.enumx.SessionEnum;
import kopo.poly.user.service.IUserInfoService;
import kopo.poly.user.service.impl.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        if (session.isNew() || ((String) session.getAttribute(SessionEnum.USER_ID.STRING)).equals("")) {
//            session.setAttribute(SessionEnum.USER_ID.STRING, "USER01");
            return "redirect:/login/login-form";
        }
        log.info(this.getClass().getName() + ".getMemberInfo() START!!!!!!!!!!!!!!");

        UserInfoDTO pDTO = new UserInfoDTO();
        String userId = (String) session.getAttribute(SessionEnum.USER_ID.STRING);

        pDTO.setUserId(userId);

        log.info("pDTO : " + pDTO.toString());

        UserInfoDTO rDTO = userInfoService.getUserInfo(pDTO);

        log.info("rDTO : " + rDTO.toString());

        model.addAttribute("rDTO", rDTO);

        log.info(this.getClass().getName() + ".getMemberInfo() START!!!!!!!!!!!!!!");
        return "/user/profile/member-info";
    }
}
