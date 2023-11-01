package kopo.poly.community.persistance.mapper;

import kopo.poly.community.dto.CommunityDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ICommunityMapper {

    //게시판 리스트
    List<CommunityDTO> getCommunityList() throws Exception;

    //게시판 글 등록
    void insertCommunityInfo(CommunityDTO pDTO) throws Exception;

    //게시판 상세보기
    CommunityDTO getCommunityInfo(CommunityDTO pDTO) throws Exception;

    //게시판 조회수 업데이트
    void updateCommunityReadCnt(CommunityDTO pDTO) throws Exception;

    //게시판 글 수정
    void updateCommunityInfo(CommunityDTO pDTO) throws Exception;

    //게시판 글 삭제
    void deleteCommunityInfo(CommunityDTO pDTO) throws Exception;
}
