package kopo.poly.user.service;

import kopo.poly.user.dto.UserInfoDTO;

public interface IUserInfoService {

    // 아이디 중복 체크
    UserInfoDTO getUserIdExists(UserInfoDTO pDTO) throws Exception;

    // 이메일 주소 중복 체크 및 인증 값
    UserInfoDTO getEmailExists(UserInfoDTO pDTO) throws Exception;

    // 회원 가입하기 (회원정보 등록하기)
    int insertBizUserInfo(UserInfoDTO pDTO) throws Exception; /* pDTO란 변수명이 아니어도 됩니다! 매개변수는 보통 p 붙인대요 */

    int insertNewBizUserInfo(UserInfoDTO pDTO) throws Exception;

    // 로그인을 위해 아이디와 비밀번호가 일치하는지 확인하기
    UserInfoDTO getLogin(UserInfoDTO pDTO) throws Exception;

    // 아이디, 비밀번호 찾기에 활용
    UserInfoDTO searchUserIdOrPasswordProc(UserInfoDTO pDTO) throws Exception;

    // 비밀번호 재설정
    //int newPasswordProc(UserInfoDTO pDTO) throws Exception;

    // 임시 비밀번호 생성 후 메일 전송
    int pwCode(UserInfoDTO pDTO) throws Exception;

    UserInfoDTO getUserInfo(UserInfoDTO pDTO) throws Exception;
}
