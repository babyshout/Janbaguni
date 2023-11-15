package kopo.poly.user.service.impl;

import kopo.poly.user.dto.MailDTO;
import kopo.poly.user.dto.UserInfoDTO;
import kopo.poly.user.persistance.mapper.IUserInfoMapper;
import kopo.poly.user.service.IMailService;
import kopo.poly.user.service.IUserInfoService;
import kopo.poly.user.util.CmmUtil;
import kopo.poly.user.util.EncryptUtil;
import kopo.poly.user.util.RandomCodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserInfoService implements IUserInfoService {

    private final IUserInfoMapper userInfoMapper; // 회원관련 SQL 사용하기 위한 Mapper 가져오기

    private final IMailService mailService; // 메일 발송을 위한 MailService 자바 객체 가져오기

    @Override
    public UserInfoDTO getUserIdExists(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getUserIdExists Start!");

        UserInfoDTO rDTO = userInfoMapper.getUserIdExists(pDTO);

        log.info("rDTO.existsYn : " + rDTO.getExistsYn());

        log.info(this.getClass().getName() + ".getUserIdExists End!");

        return rDTO;
    }

    @Override
    public UserInfoDTO getEmailExists(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".emailAuth Start!");

        log.info("pDTO email : " + pDTO.getEmail());

        // DB 이메일이 존재하는지 SQL 쿼리 실행
        // SQL 쿼리에 COUNT()를 사용하기 때문에 반드시 조회 결과는 존재함
        UserInfoDTO rDTO = userInfoMapper.getEmailExists(pDTO);

        if (rDTO == null) {
            rDTO = new UserInfoDTO();
        }

        String existsYn = CmmUtil.nvl(rDTO.getExistsYn());

        log.info("existsYn : " + existsYn);

        if (existsYn.equals("N")) {

            // 6자리 랜덤 숫자 생성하기
            int authNumber = ThreadLocalRandom.current().nextInt(100000, 1000000);

            log.info("authNumber : " + authNumber);

            // 인증번호 발송 로직
            MailDTO dto = new MailDTO();



            dto.setTitle("이메일 중복 확인 인증번호 발송 메일");
            dto.setContents("인증번호는 " + authNumber + " 입니다.");
            dto.setToMail(EncryptUtil.decAES128CBC(CmmUtil.nvl(pDTO.getEmail())));

            log.info("dto.getToMail() : " + dto.getToMail());

            mailService.doSendMail(dto); // 이메일 발송 서비스 호출

            dto = null;

            rDTO.setAuthNumber(authNumber); // 인증번호를 결과값에 넣어주기

        }

        log.info(this.getClass().getName() + ".emailAuth End!");

        return rDTO;
    }

    @Override
    public int insertBizUserInfo(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertBizUserInfo Start!");

        // 회원가입 성공 : 1, 아이디 중복으로 인한 가입 취소 : 2, 기타 에러 발생 : 0
        int res = 0;

        log.info("pDTO : " + pDTO.toString());

        res = userInfoMapper.insertBizUserInfo(pDTO);

        log.info("res : " + res);

        log.info(this.getClass().getName() + ".insertBizUserInfo End!");

        return res;
    }

    @Override
    public int insertNewBizUserInfo(UserInfoDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".insertNewBizUserInfo() START!!!!!!!!!");
        int res = 0;

        log.info("pDTO : " + pDTO.toString());

        res = userInfoMapper.insertNewBizUserInfo(pDTO);

        log.info("res : " + res);

        log.info(this.getClass().getName() + ".insertNewBizUserInfo() START!!!!!!!!!");
        return res;
    }

    /*
     * 로그인을 위해 아이디와 비밀번호가 일치하는지 확인하기
     */
    @Override
    public UserInfoDTO getLogin(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getLogin Start!");

        UserInfoDTO rDTO = Optional.ofNullable(
                userInfoMapper.getLogin(pDTO)
        ).orElseGet(UserInfoDTO::new); /* 널인 경우에는 빈 것으로 처리한다!! */

        if (CmmUtil.nvl(rDTO.getUserId()).length() > 0) {

            log.info("로그인 성공");
        }

        log.info(this.getClass().getName() + ".getLogin End!");

        return rDTO;
    }

    @Override
    public UserInfoDTO searchUserIdOrPasswordProc(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".searchUserIdOrPasswordProc Start!");

        UserInfoDTO rDTO = userInfoMapper.getUserId(pDTO);

        log.info(this.getClass().getName() + ".searchUserIdOrPasswordProc End!");

        return rDTO;
    }

    /*@Override
    public int newPasswordProc(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".newPasswordProc Start!");

        // 비밀번호 재설정
        int success = userInfoMapper.updatePassword(pDTO);

        log.info(this.getClass().getName() + ".newPasswordProc End!");

        return success;
    }*/

    /*
     * 이메일 일치 후 임시 비밀번호 발급
     */
    @Override
    public int pwCode(UserInfoDTO pDTO) throws Exception {

        log.info(getClass().getName() + ".pwCode Start!");

        int res = 0;

        try {
            // DB 이메일이 존재하는지 SQL 쿼리 실행
            // SQL 쿼리에 COUNT()를 사용하기 때문에 반드시 조회 결과는 존재함
            UserInfoDTO rDTO = userInfoMapper.getUserExists(pDTO);

            if (rDTO != null) {
                res = 1;

                // 임시 비밀번호 생성하기
                String newPwd = CmmUtil.nvl(RandomCodeUtil.createKey());

                // 임시 비밀번호 설정 및 업데이트
                UserInfoDTO nDTO = new UserInfoDTO();

                nDTO.setUserId(pDTO.getUserId());
                nDTO.setUserName(pDTO.getUserName());
                nDTO.setEmail(EncryptUtil.decAES128CBC(pDTO.getEmail()));
                nDTO.setPassword(EncryptUtil.encHashSHA256(newPwd));

                userInfoMapper.updatePassword(nDTO);

                log.info("임시 비밀번호로 업데이트 성공");

                // 인증번호 발송 로직
                MailDTO dto = new MailDTO();

                dto.setTitle("임시 비밀번호 발송 메일");
                dto.setContents("회원님의 임시 비밀번호는 " + newPwd + " 입니다.\n로그인 후 반드시 비밀번호를 변경해주세요!");
                dto.setToMail(EncryptUtil.decAES128CBC(pDTO.getEmail()));

                mailService.doSendMail(dto); // 이메일 발송 서비스 호출

                dto = null;

            } else {
                log.info("업데이트 실패");
            }

        } catch (
                Exception e) {
            res = 0;
            log.info("[ERR0R] " + this.getClass().getName() + " doSendMail : " + e);

        }

        log.info(this.getClass().getName() + ".pwCode End!");

        return res;
    }

    @Override
    public UserInfoDTO getUserInfo(UserInfoDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".getUserInfo() START!!!!!!!");

        log.info("pDTO : " + pDTO.toString());

        UserInfoDTO rDTO = userInfoMapper.getUserInfo(pDTO);

        log.info("rDTO : " + rDTO.toString());

        log.info(this.getClass().getName() + ".getUserInfo() END!!!!!!!");
        return rDTO;
    }

    /**
     * USER_TYPE 이 biz 면,
     * pDTO로, USER_ID 랑, 기타 정보를 가지고와서,
     * 해당 ROW를 pDTO 에 담긴 내용으로 전부 바꿈
     * @param pDTO
     * 바꿀 USER_ID 에 대한 정보와, 업데이트 할 정보가 있어야함
     * @throws Exception
     */
    @Override
    public int updateBizUserInfo(UserInfoDTO pDTO) throws Exception {
        log.info(this.getClass().getName()
                + ".updateBizUserInfo() START!!!!!!!!!");

        log.info(this.getClass().getName()
                + ".updateBizUserInfo() END!!!!!!!!!");
        return userInfoMapper.updateBizUserInfo(pDTO);
    }

    /**
     * USER_TYPE 이 newbiz 면,
     * pDTO로, USER_ID 랑, 기타 정보를 가지고와서,
     * 해당 ROW를 pDTO 에 담긴 내용으로 전부 바꿈
     *
     * @param pDTO 바꿀 USER_ID 에 대한 정보와, 업데이트 할 정보가 있어야함
     * @throws Exception
     */
    @Override
    public int updateNewbizUserInfo(UserInfoDTO pDTO) throws Exception {
        log.info(this.getClass().getName()
                + ".updateNewbizUserInfo() START!!!!!!!!!!!!");
        return userInfoMapper.updateNewbizUserInfo(pDTO);
    }
}
