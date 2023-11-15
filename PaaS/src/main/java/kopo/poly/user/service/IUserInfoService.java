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

    /**
     * pDTO 로 UserInfo 테이블의 pk (USER_ID)를 받아서
     * 해당 pk에 대한 정보를 전부 던져주는 service,
     * Mapper 랑 연결되어있음
     * @param pDTO 반드시 userId 를 가지고 있어야함
     * @return 해당 USER_ID 에 대해 select 쿼리 날려서, 있으면 가지고옴
     * @throws Exception 왜있는지 모름
     */

    UserInfoDTO getUserInfo(UserInfoDTO pDTO) throws Exception;

    /**
     * USER_TYPE 이 biz 면,
     * pDTO로, USER_ID 랑, 기타 정보를 가지고와서,
     * 해당 ROW를 pDTO 에 담긴 내용으로 전부 바꿈
     * @param pDTO
     * 바꿀 USER_ID 에 대한 정보와, 업데이트 할 정보가 있어야함
     * @throws Exception
     */
    int updateBizUserInfo(UserInfoDTO pDTO) throws Exception;

    /**
     * USER_TYPE 이 newbiz 면,
     * pDTO로, USER_ID 랑, 기타 정보를 가지고와서,
     * 해당 ROW를 pDTO 에 담긴 내용으로 전부 바꿈
     * @param pDTO
     * 바꿀 USER_ID 에 대한 정보와, 업데이트 할 정보가 있어야함
     * @throws Exception
     */
    int updateNewbizUserInfo(UserInfoDTO pDTO) throws Exception;
}
