package kopo.poly.community.controller;

import kopo.poly.dto.MsgDTO;
import kopo.poly.community.dto.CommunityDTO;
import kopo.poly.community.service.ICommunityService;
import kopo.poly.community.util.CmmUtil;
import kopo.poly.user.enumx.SessionEnum;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

/*
 * controller를 선언해야만 Spring 프레임워크에서 Controller인지 인식이 가능하다
 * 자바 서블릿 역할 수행
 *
 * slf4j는 스프링 프레임워크에서 로그 처리하는 인터페이스 기술이며,
 * 로그처리 기술인 log4j와 logback과 인터페이스 역할을 수행한다.
 * 스프링 프레임워크는 기본으로 logback을 채택해서 로그 처리를 한다.
 * */
@Slf4j
@RequestMapping(value = "/community") // -> /community로 시작하는 url은 무조건 community컨트롤러에서 처리
@RequiredArgsConstructor // 생성자 주입을 하기 위한 어노테이션
@Controller
@ToString
public class CommunityController {
    // @RequiredArgsConstructor를 통해 메모리에 올라간 서비스 객체를 Controller에서 사용할 수 있게 주입시켜줌
    private final ICommunityService communityService;


    /**
     * 게시판 리스트 보여주기
     * <p>
     * GetMapping(value = "community/communityList") => GET방식을 통해 접속되는 URL이 community/communityList인 경우에 아래 함수를 실행함
     */
    @GetMapping(value = "communityList")
    public String communityList(ModelMap model) throws Exception {

        //로그 찍기(추후 찍은 로그를 통해 이 함수에 접근했는지 파악한다.)
        log.info(this.getClass().getName() + ".CommunityList Start!");

        List<CommunityDTO> rList = Optional.ofNullable(communityService.getCommunityList()).orElseGet(ArrayList::new);


        //공지사항 리스트 조회하기
        // java8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        //service를 호출하여 공지사항 결과를 받아줌
//        List<CommunityDTO> rList = Optional.ofNullable(
//                CommunityService.getCommunityList()
//        ).orElseGet(ArrayList::new);


//        리스트 값 찍어보기
//        log.info("rList Size : " + Integer.toString(rList.size()));
//        for (CommunityDTO dto : rList) {
//            log.info("dto : " + dto.toString());
//        }

        //공지사항 결과를 JSP로 전달하기 위해 model 객체에 추가
        //조회된 리스트 결과값 넣어주기
        model.addAttribute("rList", rList);

        //실행됐는지 확인하기 위해 로그 찍어주기
        log.info(this.getClass().getName() + ".CommunityList End!");

        //함수 처리가 끝나고 보여줄 JSP 파일명
        // webapp/WEB-INF/views/notice/communityList.html -> jsp 파일 실행
        return "/community/communityList";
    }

    /**
     * 게시판 작성 페이지로 이동하기
     * <p>
     * 이 함수는 게시판 작성 페이지로 접근하기 위해 만듦
     * <p>
     * GetMapping(value = "notice/noticeReg") => GET방식을 통해 접속되는 URL이 notice/noticeReg 경우에 아래 함수를 실행함
     */
    @GetMapping(value = "communityReg")
    public String communityReg(HttpSession session) {
        log.info(this.getClass().getName() + ".CommunityReg Start!");

        String msg = "";



        //로그인된 사용자만 글 등록할 수 있게 설정
        //로그인 세션 받아오기
        String userId = (String) session.getAttribute(SessionEnum.USER_ID.STRING);

        if (userId == null) {
            // 로그인되지 않은 사용자에게는 접근 권한이 없으므로 다른 페이지로 리다이렉트 또는 에러 메시지 반환
            return "redirect:/login/login-form"; // 로그인 페이지로 리다이렉트
        }

        log.info(this.getClass().getName() + ".CommunityReg End!");

        //함수 처리가 끝나고 보여줄 JSP 파일명
        // community/communityReg.html
        return "/community/communityReg";
    }

    /**
     * 게시판 글 등록하기
     * <p>
     * 게시글 등록은 Ajax로 호출되기 때문에 결과는 JSON 구조로 전달해야만 한다.
     * JSON 구조로 결과 메시지를 전송하기 위해 @ResponsBody 어노테이션을 추가해야한다
     */
    @ResponseBody
    @PostMapping(value = "communityInsert")
    public MsgDTO communityInsert(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".communityInsert Start!");

        String msg = ""; //메세지 내용
        MsgDTO dto = null; //결과 메세지 구조

        try {
            //로그인된 사용자 아이디 가져오기
            String userId = (String)session.getAttribute(SessionEnum.USER_ID.STRING);
            String title = CmmUtil.nvl(request.getParameter("title")); //제목
            String communityYn = CmmUtil.nvl(request.getParameter("communityYn")); //공지글 여부
            String contents = CmmUtil.nvl(request.getParameter("contents")); //내용
            String regDt = CmmUtil.nvl(request.getParameter("regDt"));

            /*
             * ###################################################################
             * 반드시, 값을 받았으면, 꼭 로그를 찍어서 제대로 들어오는지 파악해야함 반드시 작성하기!!
             * */

            log.info("session user_id : " + userId);
            log.info("title : " + title);
            log.info("communityYn : " + communityYn);
            log.info("contents : " + contents);

            //데이터를 저장하기 위해 DTO에 값 넣어주기
            CommunityDTO pDTO = new CommunityDTO(); // 값을 넣어주기 위해 pDTO 생성
            pDTO.setUserId(userId);
            pDTO.setTitle(title);
            pDTO.setCommunityYn(communityYn);
            pDTO.setContents(contents);
            pDTO.setRegDt(regDt);

            /*
             * 게시글을 등록하기 위한 비지니스 로직을 호출 ( 서비스에 작성한 로직 )
             *
             */
            communityService.insertCommunityInfo(pDTO); // INoticeService 함수를 호출함

            //저장이 완료되면 사용자에게 보여줄 메시지 작성
            msg = userId+"님의 글이 등록되었습니다."; // 서비스 호출이 정상적으로 작동하면 "등록되었습니다." 메세지를 전달하기 위해 문자열 저장하기
        } catch (Exception e) { //catch 구문은 서비스 호출 중 오류가 발생되면 실행되기 때문에 "실패하였습니다." 문자열 저장
            //저장이 실패되면 사용자에게 보여줄 메세지
            msg = "글 등록에 실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally { //메세지 문자열을 JSON 구조로 변경하기 위해 MsgDTO 객체를 생성 후, 메세지 저장하기
            //결과 메세지 전달하기
            dto = new MsgDTO(); //AJAX에 전달하는 JSON 결과 구조
            dto.setMsg(msg);

            log.info(this.getClass().getName() + ".CommunityInsert End!");
        }
        return dto; //@ResponseBody 어노테이션으로인해 자동으로 JSON 구조로 변경되어 전달됨
    }

    /**
     * 게시판 상세보기
     */
    @GetMapping(value = "communityInfo")
    public String communityInfo(HttpServletRequest request, ModelMap model,
                                HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".communityInfo Start!");


        //로그인 정보 가져오기
        String userId = (String) session.getAttribute(SessionEnum.USER_ID.STRING);
        String communitySeq = CmmUtil.nvl(request.getParameter("communitySeq")); // 커뮤니티글번호 pk
        String title = CmmUtil.nvl(request.getParameter("title")); //제목
        String contents = CmmUtil.nvl(request.getParameter("contents")); // 글 내용
        String regDt = CmmUtil.nvl(request.getParameter("regDt")); // 작성 날짜

        log.info("userId : " + session.getAttribute(SessionEnum.USER_ID.STRING));
        /*
         * 로그 확인하기
         * */
        log.info("communitySeq : " + communitySeq);


        /*값 전달을 반드시 DTO 객체를 이용해서 처리할 전달 받은 값을 DTO 객체에 넣는다.*/
        CommunityDTO pDTO = new CommunityDTO();
        pDTO.setCommunitySeq(communitySeq);
        pDTO.setUserId(userId);
        pDTO.setTitle(title);
        pDTO.setContents(contents);
        pDTO.setRegDt(regDt);



        //커뮤니티 상세정보 가져오기
        CommunityDTO rDTO = Optional.ofNullable(
                communityService.getCommunityInfo(pDTO, true)
        ).orElseGet(CommunityDTO::new);

        log.info("rDTO : " + rDTO.toString());
        //조회된 리스트 결과값 넣어주기
        model.addAttribute("rDTO", rDTO);
        log.info(this.getClass().getName() + ".communityInfo End!");

        return "community/communityInfo";
    }

    /*게시판 수정하기
     * */

    @GetMapping(value = "communityEditInfo")
    public String communityEditInfo(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + ".communityEditInfo Start!");



        String userId = CmmUtil.nvl((String) session.getAttribute(SessionEnum.USER_ID.STRING));
        String communitySeq = CmmUtil.nvl(request.getParameter("communitySeq")); // 커뮤니티글번호 pk
        String title = CmmUtil.nvl(request.getParameter("title")); //제목
        String contents = CmmUtil.nvl(request.getParameter("contents")); // 글 내용


        //로그 꼭 찍어주기
        log.info("userId : " + userId);
        log.info("communitySeq : " + communitySeq);
        log.info("title: "+ title);
        log.info("contents : " + contents);


        //DTO 객체를 이용해 전달 받은 값을 DTO 객체에 넣어주기
        CommunityDTO pDTO = new CommunityDTO();
        pDTO.setUserId(userId);
        pDTO.setCommunitySeq(communitySeq);
        pDTO.setTitle(title);
        pDTO.setContents(contents);

        CommunityDTO rDTO = Optional.ofNullable(
                communityService.getCommunityInfo(pDTO, false)
        ).orElseGet(CommunityDTO::new);

        log.info("pDTO : " + pDTO.toString());


        //조회된 리스트 결과값 넣어주기
        model.addAttribute("rDTO", rDTO);

        log.info(this.getClass().getName() + ".communityEditInfo End!");

        return "community/communityEditInfo";
    }

    /*게시판 글 수정*/
    @ResponseBody
    @PostMapping(value = "communityUpdate")
    public MsgDTO communityUpdate(HttpSession session, HttpServletRequest request) {
        log.info(this.getClass().getName() + ".communityUpdate Start!");

        String msg = "";
        MsgDTO dto = null; //결과 메세지 구조

        try {
            //로그인된 사용자 아이디 가져오기
            //로그인을 아직 구현 x 이기 때문에, 공지사항 리스트에서 로그인 한 것처럼 Session 값을 저장
            String userId = CmmUtil.nvl((String) session.getAttribute(SessionEnum.USER_ID.STRING));
            String communitySeq = CmmUtil.nvl(request.getParameter("communitySeq")); //글번호 PK
            String title = CmmUtil.nvl(request.getParameter("title")); //제목
            String communityYn = CmmUtil.nvl(request.getParameter("communityYn")); //공지글 여부
            String contents = CmmUtil.nvl(request.getParameter("contents")); //내용

            /*
             * ###################################################################
             * 반드시, 값을 받았으면, 꼭 로그를 찍어서 제대로 들어오는지 파악해야함 반드시 작성하기!!
             * */

            log.info("session user_id : " + userId);
            log.info("communitySeq : " + communitySeq );
            log.info("title : " + title);
            log.info("communityYn : " + communityYn);
            log.info("contents : " + contents);


            //데이터를 저장하기 위해 DTO에 값 넣어주기
            CommunityDTO pDTO = new CommunityDTO(); // 값을 넣어주기 위해 pDTO 생성
            pDTO.setUserId(userId);
            pDTO.setCommunitySeq(communitySeq);
            pDTO.setTitle(title);
            pDTO.setCommunityYn(communityYn);
            pDTO.setContents(contents);

            //게시글 수정하기 DB
            communityService.updateCommunityInfo(pDTO);

            msg = "수정되었습니다.";
        } catch (Exception e) {
            msg = "실패하였습니다." + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            //결과 메세지 전달하기
            dto = new MsgDTO();
            dto.setMsg(msg);

            log.info(this.getClass().getName() + ".communityUpdate End!");
        }
        return dto;
    }

    /*
     * 게시판 글 삭제하기*/
    @ResponseBody //클라이언트가 보낸 데이터를 읽어들임 값을 파라미터로 전달받아 NoticeService에 전달해줌
    @PostMapping(value = "communityDelete")
    public MsgDTO communityDelete(HttpServletRequest request, HttpSession session) {
        log.info(this.getClass().getName() + ".communityDelete Start!");

        String msg = ""; //메세지 내용
        MsgDTO dto = null; //결과 메세지 구조

        try {

            String communitySeq = CmmUtil.nvl(request.getParameter("communitySeq")); //글번호 (PK)
            String userId = CmmUtil.nvl((String) session.getAttribute(SessionEnum.USER_ID.STRING));


            //로그 찍어주기
            log.info("cSeq : " + communitySeq);
            log.info("userId : " + userId);

            // 값 전달은 DTO 객체를 이용해 처리할 전달 받은 값을 DTO 객체에 넣음
            CommunityDTO pDTO = new CommunityDTO();
            pDTO.setCommunitySeq(communitySeq);
            pDTO.setUserId(userId);

            //DB에서 게시글 삭제하기
            communityService.deleteCommunityInfo(pDTO); //전달 받은 값을 DTO에 저장했으니 INoticeService안에 있는 delete함수를 호출해 값을 처리한다.

            msg = "삭제되었습니다.";
        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            dto = new MsgDTO();
            dto.setMsg(msg);

            log.info(this.getClass().getName() + ".communityDelete End!");
        }

        return dto;
    }
    @ResponseBody
    @GetMapping(value = "/communitySearch")
    public MsgDTO searchList(HttpServletRequest request, ModelMap model) {
        log.info(this.getClass().getName() + "communitySearch Start!");

        String msg = "";
        MsgDTO dto = new MsgDTO(); // 결과 메세지 구조

        try {
            String keyWord = CmmUtil.nvl(request.getParameter("keyWord"));

            log.info("keyWord : " + keyWord);

            CommunityDTO pDTO = new CommunityDTO();
            pDTO.setKeyWord(keyWord);

//             communityService.getSearchKeyWord 메소드를 호출하여 검색 결과를 가져오기
            communityService.getSearchKeyWord(pDTO);


            msg = "검색되었습니다.";

        } catch (Exception e) {
            msg = "에러가 발생하였습니다." + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            // MsgDTO에 메시지 설정
            dto.setMsg(msg);
            log.info(this.getClass().getName() + ".communitySearch End!");
        }

        return dto;
    }

//    @GetMapping(value = "about")
//    public String testMain(){
//        return "communityList";
//    }


}
