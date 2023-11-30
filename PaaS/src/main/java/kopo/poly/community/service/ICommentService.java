package kopo.poly.community.service;

import kopo.poly.community.dto.CommentDTO;
import kopo.poly.community.dto.CommunityDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ICommentService {

    /**
     * 댓글 리스트
     * @return
     * @throws Exception
     */
    List<CommentDTO> getCommentList(CommunityDTO pDTO) throws Exception;


    /**
     * 댓글 등록
     * @param pDTO
     * @throws Exception
     */
    void insertCommentInfo(CommentDTO pDTO) throws Exception;

    /**
     * 댓글 수정
     * @param pDTO
     * @throws Exception
     */
    void updateCommentInfo(CommentDTO pDTO) throws Exception;

    /**
     * 댓글 삭제
     * @param pDTO
     * @throws Exception
     */
    void deleteCommentInfo(CommentDTO pDTO) throws Exception;


}
