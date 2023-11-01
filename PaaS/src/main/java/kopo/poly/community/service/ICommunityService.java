package kopo.poly.community.service;

import kopo.poly.community.dto.CommunityDTO;

import java.util.List;

public interface ICommunityService {


    /**
     *
     * @return
     * @throws Exception
     */
    List<CommunityDTO> getCommunityList() throws Exception;

    /**
     * 공지사항 상세보기
     *
     * @param pDTO 상세내용 조회할 noticeSeq 값
     * @param type 조회수 증가여부(수정보기는 조회수 증가하지 않음)
     * @return 조회 결과
     */
    CommunityDTO getCommunityInfo(CommunityDTO pDTO, boolean type) throws Exception;

    /**
     * 공지사항 등록
     *
     * @param pDTO 화면에서 공지사항 입력된 값들 보여주기
     */
    void insertCommunityInfo(CommunityDTO pDTO) throws Exception;

    /**
     * 공지사항 수정
     *
     * @param pDTO 화면에서 입력된 값이 수정되기 위한 공지사항에 입력된 값들 보여주기
     */
    void updateCommunityInfo(CommunityDTO pDTO) throws Exception;

    /**
     * 공지사항 글 삭제하기
     *
     * @param pDTO 삭제할 noticeSeq 값
     */
    void deleteCommunityInfo(CommunityDTO pDTO) throws Exception;


}
