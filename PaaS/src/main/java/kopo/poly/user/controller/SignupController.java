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
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor    // private final 사용 안하면 적용안됨
@RequestMapping(value = "signup")
@Controller
public class SignupController {

    private final IUserInfoService userInfoService;

    /*
     * 회원가입 화면으로 이동
     * */
    @GetMapping(value = "/select")
    public String signupSelect() {

        log.info(this.getClass().getName() + "./signup/select start!");

        return "user/bingguxi/signup/signup-select";
    }

    @GetMapping(value = "/biz")
    public String signupBiz() {

        log.info(this.getClass().getName() + "./signup/biz start!");

        return "user/bingguxi/signup/signup-biz";
    }

    //사업자등록증 진위여부 확인 페이지
    @GetMapping(value = "/checktax")
    public String signupCheckTax() {

        log.info(this.getClass().getName() + "./signup/checktax start!");

        return "user/bingguxi/signup/signup-checktax";
    }

    @GetMapping(value = "/tax")
    public String signupTax() {
        log.info(this.getClass().getName() + "./signup/tax start!");

        return "user/bingguxi/signup/signup-tax";
    }

    /*
     * 회원 가입 전 아이디 중복체크하기 (Ajax를 통해 입력한 아이디 정보 받음)
     * */
    @ResponseBody
    @PostMapping(value = "/getUserIdExists")
    public UserInfoDTO getIdExists(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".getUserIdExists Start!");

        String userId = CmmUtil.nvl(request.getParameter("userId")); // 회원아이디

        log.info("userId : " + userId);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserId(userId);


        // 회원아이디를 통해 중복된 아이디인지 조회
        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getUserIdExists(pDTO)
        ).orElseGet(UserInfoDTO::new);

        log.info(this.getClass().getName() + ".getUserIdExists End!");

        return rDTO;
    }

    /*
     * 회원 가입 전 이메일 중복체크하기(Ajax를 통해 입력한 아이디 정보 받음)
     * 유효한 이메일인지 확인하기 위해 입력된 이메일에 인증번호 포함하여 메일 발송
     * */
    @ResponseBody
    @PostMapping(value = "/getEmailExists")
    public UserInfoDTO getEmailExists(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".getEmailExists Start!");

        String email = CmmUtil.nvl(request.getParameter("email")); // 회원 아이디

        log.info("email : " + email);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setEmail(EncryptUtil.encAES128CBC(email));

        // 입력된 이메일이 중복된 이메일인지 조회
        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getEmailExists(pDTO)).orElseGet(UserInfoDTO::new);

        log.info(this.getClass().getName() + ".getEmailExists End!");

        return rDTO;
    }

    /*
     * 회원가입 버튼 클릭시
     * */
    @PostMapping(value = "/insertUserInfo")
    public String insertUserInfo(HttpServletRequest request, ModelMap modelMap) throws Exception {

        log.info(this.getClass().getName() + ".insertUserInfo start!");

        int res;
        String msg = ""; // 회원가입 결과에 대한 메시지를 전달할 변수
        String url = "";

        // 웹(회원정보 입력화면)에서 받는 정보를 저장할 변수
        UserInfoDTO pDTO = null;

        try {
            String userId = CmmUtil.nvl(request.getParameter("userId")); // 아이디
            String userName = CmmUtil.nvl(request.getParameter("userName")); // 이름
            String nickname = CmmUtil.nvl(request.getParameter("nickname"));
            String password = CmmUtil.nvl(request.getParameter("password")); // 비밀번호
            String email = CmmUtil.nvl(request.getParameter("email")); // 이메일
            String addr1 = CmmUtil.nvl(request.getParameter("addr1")); // 주소
            String addr2 = CmmUtil.nvl(request.getParameter("addr2")); // 상세주소
            String job = CmmUtil.nvl(request.getParameter("job")); // 주요 분야
            String userType = CmmUtil.nvl(request.getParameter("userType")); // 회원유형

            log.info("userId : " + userId);
            log.info("userName : " + userName);
            log.info("nickname : " + nickname);
            log.info("password : " + password);
            log.info("email : " + email);
            log.info("addr1 : " + addr1);
            log.info("addr2 : " + addr2);
            log.info("userType : " + userType);
            log.info("job : " + job);

            // 웹(회원정보 입력화면)에서 받는 정보를 저장할 변수를 메모리에 올리기
            pDTO = new UserInfoDTO();

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

            /*
             * 회원가입
             * */
            if (pDTO.getUserType().equals("biz")) {
                res = userInfoService.insertBizUserInfo(pDTO);
            } else {
                res = userInfoService.insertNewBizUserInfo(pDTO);
            }

            log.info("회원가입 결과(res) : " + res);

            if (res == 1) {
                msg = "회원가입되었습니다.";
                url = "/login/login-form";

            } else {
                msg = "오류로 인해 회원가입이 실패하였습니다. 다시 시도해주세요.";

            }
        } catch (Exception e) {
            // 저장이 실패되면 사용자에게 보여줄 메시지
            msg = "실패하였습니다. : " + e;
            log.error(e.toString());
            e.printStackTrace();

        } finally {
            modelMap.addAttribute("msg", msg); /* ("key",value) */
            modelMap.addAttribute("url", url);

            log.info(this.getClass().getName() + ".insertUserInfo End!");
        }

        return "/redirect";
    }
}