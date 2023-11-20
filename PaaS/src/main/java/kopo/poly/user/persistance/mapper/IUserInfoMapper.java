package kopo.poly.user.persistance.mapper;

import kopo.poly.user.dto.UserInfoDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IUserInfoMapper {

    // 회원 가입하기 (회원정보 등록하기)
    int insertBizUserInfo(UserInfoDTO pDTO) throws Exception;

    /**
     * USER_TYPE 이 newbiz 면,
     * pDTO 에 있는걸 newbiz 로 넣어버림
     * @param pDTO
     * @return
     * @throws Exception
     */
    int insertNewBizUserInfo(UserInfoDTO pDTO) throws Exception;

    // 회원 가입 전 아이디 중복체크하기 (DB 조회하기)
    UserInfoDTO getUserIdExists(UserInfoDTO pDTO) throws Exception;

    // 회원 가입 전 이메일 중복체크하기 (DB 조회하기)
    UserInfoDTO getEmailExists(UserInfoDTO pDTO) throws Exception;

    // 로그인을 위해 아이디와 비밀번호가 일치하는지 확인하기
    UserInfoDTO getLogin(UserInfoDTO pDTO) throws Exception;

    // 아이디, 비밀번호 찾기에 활용
    UserInfoDTO getUserId(UserInfoDTO pDTO) throws Exception;

    // 비밀번호 재설정
    //int updatePassword(UserInfoDTO pDTO) throws Exception;

    // 비밀번호 찾기시 회원정보 유무여부 확인하기
    UserInfoDTO getUserExists(UserInfoDTO pDTO) throws Exception;

    // 임시 비밀번호로 비밀번호 재설정
    void updatePassword(UserInfoDTO pDTO) throws Exception;

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
