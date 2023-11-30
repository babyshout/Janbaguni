package kopo.poly.community.controller;

import kopo.poly.community.dto.CommentDTO;
import kopo.poly.community.dto.CommunityDTO;
import kopo.poly.community.service.ICommentService;
import kopo.poly.community.service.ICommunityService;
import kopo.poly.dto.MsgDTO;
import kopo.poly.user.enumx.SessionEnum;
import kopo.poly.user.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequestMapping(value = "/comment")
@Slf4j
@RequiredArgsConstructor
@RestController
public class CommentController {

    private final ICommentService commentService;

    @ResponseBody
    @PostMapping(value = "commentInsert")
    public MsgDTO commentInsert(HttpServletRequest request, HttpSession session){

        log.info(this.getClass().getName()+ "commentInsert Start!");

        String msg = ""; //메세지 내용
        MsgDTO dto = null; //메세지 구조

        try{
            String writer = (String)session.getAttribute(SessionEnum.USER_ID.STRING);
            //지금 이 communitySeq가 null인데 이걸 해당 게시물 번호를 받아와야하는데 어떻게 받아오지
            String communitySeq = CmmUtil.nvl(request.getParameter("communitySeq"));
            String contents = CmmUtil.nvl(request.getParameter("content")); //댓글 내용
            String wDate = CmmUtil.nvl(request.getParameter("wDate")); //댓글 작성 날짜

            log.info("writer : " + writer);
            log.info("communitySeq : " + communitySeq);
            log.info("contents : " + contents);
            log.info("wDate : " + wDate);

            // 데이터를 저장하기 위해 DTO에 값 넣어주기
            CommentDTO pDTO = new CommentDTO();

            pDTO.setWriter(writer);
            pDTO.setCommunitySeq(communitySeq);
            pDTO.setContents(contents);
            pDTO.setWdate(wDate);

            commentService.insertCommentInfo(pDTO);

            msg = writer + "님의 댓글이 등록되었습니다.";

        } catch (Exception e) {
            msg = "댓글 등록에 실패했습니다: " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            dto = new MsgDTO(); // AJAX에 전달할 JSON으로
            dto.setMsg(msg);

            log.info(this.getClass().getName() + ".commentInsert End!");
        }

        return dto;
    }


    /**
     * 게시판 글 수정
     */
    @ResponseBody
    @PostMapping(value = "commentUpdateInfo")
    public MsgDTO commentUpdate(HttpSession session, HttpServletRequest request){
        log.info(this.getClass().getName()+ " .commentUpdate Start!");

        String msg = "";
        MsgDTO dto = null; //결과 메시지 구조

        try{
            String writer = (String)session.getAttribute(SessionEnum.USER_ID.STRING);
            String rNO = CmmUtil.nvl(request.getParameter("rNO")); //댓글 번호
            String contents = CmmUtil.nvl(request.getParameter("contents")); //제목
            String wDate = CmmUtil.nvl(request.getParameter("wDate")); //댓글 작성날짜

            log.info("writer : "+ writer);
            log.info("rNO : " + rNO);
            log.info("contents : " + contents);
            log.info("wDate : " + wDate);

            //데이터를 저장하기 위해 DTO에 값 넣어주기
            CommentDTO pDTO = new CommentDTO(); //pDTO 생성
            pDTO.setWriter(writer);
            pDTO.setRNO(rNO);
            pDTO.setContents(contents);
            pDTO.setWdate(wDate);

            commentService.updateCommentInfo(pDTO);

            msg = "수정되었습니다.";
        } catch (Exception e) {
            msg = "실패하였습니다." + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        }finally {
            dto = new MsgDTO();
            dto.setMsg(msg);
            log.info(this.getClass().getName()+".commentUpdate End!");
        }
        return dto;
    }

    /**
     * 댓글 삭제
     */
    @ResponseBody
    @PostMapping(value = "commentDelete")
    public MsgDTO commentDelete(HttpServletRequest request, HttpSession session){
        log.info(this.getClass().getName()+".commentDelete Start!");

        String msg = "";
        MsgDTO dto = null; //결과 메세지 구조

        try {
            String rNO = CmmUtil.nvl(request.getParameter("rNO")); //댓글번호


            //로그 찍어주기
            log.info("rNO : " + rNO); //댓글 번호

            // 값 전달은 DTO 객체를 이용해 처리할 전달 받은 값을 DTO 객체에 넣음
            CommentDTO pDTO = new CommentDTO();
            pDTO.setRNO(rNO); // 댓글 번호

            //DB에서 댓글 삭제
            commentService.deleteCommentInfo(pDTO);

            msg = "삭제되었습니다.";
        }catch (Exception e){
            msg = "실패하였습니다 : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        }finally {
            dto = new MsgDTO();
            dto.setMsg(msg);
        }
        return dto;
    }
}
