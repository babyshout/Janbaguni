package kopo.poly.community.service.impl;

import kopo.poly.community.dto.CommentDTO;
import kopo.poly.community.dto.CommunityDTO;
import kopo.poly.community.persistance.mapper.ICommentMapper;
import kopo.poly.community.service.ICommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService implements ICommentService {

    private final ICommentMapper commentMapper;

    /**
     * 댓글 리스트 가져오기
     * @return
     * @throws Exception
     */
    @Override
    public List<CommentDTO>  getCommentList(CommunityDTO pDTO) throws Exception{
        log.info(this.getClass().getName()+ " .getCommentList Start!");

        return commentMapper.getCommentList(pDTO);
    }




    /**
     * 댓글 등록
     * @param pDTO
     * @throws Exception
     */
    @Transactional
    @Override
    public void insertCommentInfo(CommentDTO pDTO) throws Exception {

        log.info(this.getClass().getName()+".insertCommentInfo Start!");

        commentMapper.insertCommentInfo(pDTO);

    }

    /**
     * 댓글 수정
     * @param pDTO
     * @throws Exception
     */

    @Transactional
    @Override
    public void updateCommentInfo(CommentDTO pDTO) throws Exception {
        log.info(this.getClass().getName()+".updateCommentInfo Start!");

        commentMapper.updateCommentInfo(pDTO);
    }


    /**
     * 댓글 삭제
     * @param pDTO
     * @throws Exception
     */
    @Transactional
    @Override
    public void deleteCommentInfo(CommentDTO pDTO) throws Exception {
        log.info(this.getClass().getName()+".deleteCommentInfo Start!");

        commentMapper.deleteCommentInfo(pDTO);

    }
}
