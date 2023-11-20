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
import java.util.Optional;


/**
 * Profile 컨트롤러에서 회원정보 보기,, 회원정보 수정 같은거 해줄거임
 * 로그인 되어있으면 profile 띄워주고 안되어있으면 돌려보내기
 * 회원정보 수정하고싶으면 일단 password 확인하는거 한번 하고,
 * 그다음에 수정페이지로 보내주기
 * 수정페이지에서 password 확인하는거 거쳐왔는지 확인 해줘야함
 * password 확인하는거에서 session에 값 넣어주고,
 * 아니면 비밀번호 확인하는데로 보내면 될듯
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/profile")
public class ProfileController {
    private final IUserInfoService userInfoService;

    @GetMapping(value = "info")
    public String profileInfo(
            HttpSession session,
            HttpServletRequest servletRequest,
            Model model
    ) throws Exception {
        log.info(this.getClass().getName() + ".getMemberInfo() START!!!!!!!!!!!!!!");
        // 유효한 세션아이디가 아니면 /login/login-form 으로 redirect
        if (!isValidSessionUserId(session)) {
//            session.setAttribute(SessionEnum.USER_ID.STRING, "USER01");
            return "redirect:/login/login-form";
        }

        UserInfoDTO pDTO = new UserInfoDTO();
        String userId = (String) session.getAttribute(SessionEnum.USER_ID.STRING);

        pDTO.setUserId(userId);

        log.info("pDTO : " + pDTO.toString());

        UserInfoDTO rDTO = Optional.ofNullable(
                userInfoService.getUserInfo(pDTO)
        ).orElseGet(UserInfoDTO::new);
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
     *
     * @param session 이 메서드를 호출한 컨트롤러에서 받아옴
     * @return null 이나 "" 이 아니면 true
     */
    private boolean isValidSessionUserId(HttpSession session) {
        return !(session.isNew() || ((String) session.getAttribute(SessionEnum.USER_ID.STRING)).equals(""));
    }

    /**
     * 그냥 password check 해주는 form 불러옴
     *
     * @param session            이거로 검사해서 id 안들어있으면 login으로 보내버림
     * @param httpServletRequest
     * @return
     */
    @GetMapping(value = "check-password-form")
    public String checkPasswordForm(
            HttpSession session,
            HttpServletRequest httpServletRequest
    ) {
        log.info(this.getClass().getName() + "\tcheck-password-form CALLED!!!!!!!!!");
        if (!isValidSessionUserId(session)) {
            return "redirect:/login/login-form";
        }

        return "/user/profile/update/profile-password-check";
    }

    /**
     * /profile/check-password-form 에서 요청 받아서
     * 맞으면 session에 값 넣어주고, 프로필 수정하는 화면으로 보내줌
     *
     * @param session  session에서 id 받아오고, 값 넣어줌
     * @param request
     * @param password
     * @return
     * @throws Exception
     */
    @PostMapping(value = "check-password-proc")
    public String checkPassword(
            HttpSession session,
            HttpServletRequest request,
            @RequestParam(name = "password") String password
    ) throws Exception {
        log.info(this.getClass().getName() + ".checkPassword() START!!!!!!!!!!");
        if (!isValidSessionUserId(session)) {
            return "redirect:/login/login-form";
        }

        // 여기서 이제 비밀번호 확인하고, 아니면 가만히 있을거임
        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setPassword(
                EncryptUtil.encHashSHA256(CmmUtil.nvl(password))
        );
        pDTO.setUserId(
                CmmUtil.nvl(
                        (String) session.getAttribute(SessionEnum.USER_ID.STRING)
                )
        );
        log.info("pDTO : " + pDTO.toString());

        UserInfoDTO rDTO = Optional.ofNullable(
                userInfoService.getUserInfo(pDTO)
        ).orElseGet(UserInfoDTO::new);

        if (!rDTO.getPassword().equals(pDTO.getPassword())) {
            return "redirect:/profile/check-password-form";
        }

//        session.setAttribute(ProfileEnum.CHECKED, ProfileEnum.valueOf());
        session.setAttribute(ProfileEnum.CHECKED.STRING, "yes");

        return "redirect:/profile/update-form";
//        return "/user/profile/edit/profile-password-check";
//        return "redirect:/profile/check-password";

//        UserInfoDTO rDTO;
//
//        log.info(this.getClass().getName() + ".checkPassword() END!!!!!!!!!!");
//        if ((rDTO.getUserType().equals("biz"))) {
//            return "/user/profile/edit/profie-biz-edit";
//        }

    }

    @GetMapping(value = "/update-form")
    public String updateForm(
            HttpSession session,
            Model model
    ) throws Exception {
        log.info(this.getClass().getName() + ".updateForm() START!!!!!!!!!!!");
        log.info(
                "session.getAttribute(SessionEnum.USER_ID.STRING)"
                + session.getAttribute(SessionEnum.USER_ID.STRING)
        );
        if (!isValidSessionUserId(session)) {
            return "redirect:/login/login-form";
        }
        log.info("session.getAttribute(ProfileEnum.CHECKED.STRING)" +
                session.getAttribute(ProfileEnum.CHECKED.STRING));
        log.info(
                "session.getAttribute(ProfileEnum.CHECKED.STRING)).equals(\"yes\")"
                + ((String)session.getAttribute(ProfileEnum.CHECKED.STRING)).equals("yes")
        );
        if (!((String) session.getAttribute(ProfileEnum.CHECKED.STRING)).equals("yes")) {
            return "redirect:/profile/check-password-form";
        }
        UserInfoDTO pDTO = new UserInfoDTO();
        String userId = (String) session.getAttribute(SessionEnum.USER_ID.STRING);

        pDTO.setUserId(userId);
        log.info("pDTO : " + pDTO.toString());

        UserInfoDTO rDTO = Optional.ofNullable(
                userInfoService.getUserInfo(pDTO)
        ).orElseGet(UserInfoDTO::new);

        rDTO.setEmail(
                EncryptUtil.decAES128CBC(CmmUtil.nvl(rDTO.getEmail()))
        );

        log.info("USER_TYPE : " + rDTO.getUserType());
        log.info("rDTO : " + rDTO.toString());

        model.addAttribute("rDTO", rDTO);


        log.info(this.getClass().getName() + ".updateForm() END!!!!!!!!!!!");
        if (rDTO.getUserType().equals("biz")) {
            log.info(
                    "profile-biz-update called!!!!!!!!"
            );
            return "/user/profile/update/profile-biz-update";
        } else {
            log.info(
                    "profile-newbiz-update called!!!!!!!!"
            );
            return "/user/profile/update/profile-newbiz-update";
        }

//        return "/user/profile/update/update-form";
    }

    @PostMapping(value = "/update-procedure")
    public String updateProcedure(
            HttpSession session,
            HttpServletRequest request
    ) throws Exception {
        log.info(this.getClass().getName() + ".updateProcedure START!!!!!!!!!!!");

        UserInfoDTO pDTO = new UserInfoDTO();

        log.info("SessionEnum.USER_ID.name()" +
                SessionEnum.USER_ID.name());
        String userId = CmmUtil.nvl(
                (String) session.getAttribute(SessionEnum.USER_ID.STRING)
        ); // 아이디
        String userName = CmmUtil.nvl(request.getParameter("userName")); // 이름
        String nickname = CmmUtil.nvl(request.getParameter("nickname"));
        String password = CmmUtil.nvl(request.getParameter("password")); // 비밀번호
        String email = CmmUtil.nvl(request.getParameter("email")); // 이메일
        String addr1 = CmmUtil.nvl(request.getParameter("addr1")); // 주소
        String addr2 = CmmUtil.nvl(request.getParameter("addr2")); // 상세주소
        String job = CmmUtil.nvl(request.getParameter("job")); // 주요 분야
        String userType = CmmUtil.nvl(request.getParameter("userType")); // 회원유형
//        pDTO.setUserId(
//                (String) session.getAttribute(SessionEnum.USER_ID.STRING)
//        );
//        log.info("pDTO : " + pDTO.toString());
//        UserInfoDTO rDTO = Optional.ofNullable(
//                userInfoService.getUserInfo(pDTO)
//        ).orElseGet(UserInfoDTO::new);
        // FIXME
        //  이런 느낌으로 Enum 쓰면 좋을것 같음
//        if (session.getAttribute(
//                SessionEnum.USER_TYPE.BIZ
//        ))
        pDTO.setUserId(userId); /*getter setter 써서 쓸 수 있는거 */
        pDTO.setUserName(userName);

        pDTO.setNickname(nickname);

        pDTO.setPassword(EncryptUtil.encHashSHA256(password)); /* 암호화를 한번 거쳤다 (복구화 안됨. 못 돌려보여줌) */

        pDTO.setEmail(EncryptUtil.encAES128CBC(email)); /* 복구화 가능. 잃어버렸을 때 돌려보여줄 수 있음 */
        pDTO.setAddr1(addr1);
        pDTO.setAddr2(addr2);
        pDTO.setJob(job);
        pDTO.setUserType(userType);

        log.info("pDTO : " + pDTO.toString());


        if (!((String) session.getAttribute(SessionEnum.USER_TYPE.STRING)).equals("biz")) {
            userInfoService.updateBizUserInfo(pDTO);
        } else {
            userInfoService.updateNewbizUserInfo(pDTO);
        }

        session.removeAttribute(ProfileEnum.CHECKED.STRING);
        log.info(this.getClass().getName() + ".updateProcedure END!!!!!!!!!!!");
        return "redirect:/profile/info";
    }

//    private void validSessionOrLoginForm(HttpSession session) {
//        
//    }


    enum ProfileEnum {
        CHECKED("PASSWORD_HAS_CHECKED");

        public final String STRING;

        ProfileEnum(String passwordHasChecked) {
            this.STRING = passwordHasChecked;
        }
    }
}
