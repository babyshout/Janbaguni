package kopo.poly.user.persistance.mapper;

import kopo.poly.user.dto.UserInfoDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IUserInfoMapper {

    // 회원 가입하기 (회원정보 등록하기)
    int insertUserInfo(UserInfoDTO pDTO) throws Exception;

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

    UserInfoDTO getUserInfo(UserInfoDTO pDTO) throws Exception;
}
