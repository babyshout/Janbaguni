package kopo.poly.community.controller;

import kopo.poly.dto.MsgDTO;
import kopo.poly.community.dto.CommunityDTO;
import kopo.poly.community.service.ICommunityService;
import kopo.poly.community.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;


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
    private final ICommunityService CommunityService;

    /**
     * 게시판 리스트 보여주기
     * <p>
     * GetMapping(value = "notice/noticeList") => GET방식을 통해 접속되는 URL이 notice/noticeList인 경우에 아래 함수를 실행함
     */
    @GetMapping(value = "communityList")
    public String communityList(HttpSession session, ModelMap model) throws Exception {

        //로그 찍기(추후 찍은 로그를 통해 이 함수에 접근했는지 파악한다.)
        log.info(this.getClass().getName() + ".CommunityList Start!");

//        List<CommunityDTO> rList = CommunityService.getCommunityList();

        //로그인된 사용자 아이디는 Session에 저장함
        // 교육용으로 아직 로그인을 구현하지 않았기 때문에 Session에 데이터를 저장하지 않았음
        // 추후 로그인을 구현할 것으로 가정하고, 공지사항 리스트를 출력하는 함수에서 로그인 한 것처럼 Session 값을 임의로 생성해준다.
//        session.setAttribute("SESSION_USER_ID", "USER01");

        //공지사항 리스트 조회하기
        // java8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        //service를 호출하여 공지사항 결과를 받아줌
//        List<CommunityDTO> rList = Optional.ofNullable(
//                CommunityService.getCommunityList()
//        ).orElseGet(ArrayList::new);


//        리스트 값 찍어보기
//        log.info("rList Size : "+Integer.toString(rList.size()));
//        for (CommunityDTO dto : rList) {
//            log.info("dto : " + dto.toString());
//        }


        //공지사항 결과를 JSP로 전달하기 위해 model 객체에 추가
        //조회된 리스트 결과값 넣어주기
//        model.addAttribute("rList", rList);

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
    public String communityReg() {
        log.info(this.getClass().getName() + ".CommunityReg Start!");

        log.info(this.getClass().getName() + ".CommunityReg End!");

        //함수 처리가 끝나고 보여줄 JSP 파일명
        // webapp/WEB-INF/views/notice/communityReg.html
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
            //로그인을 아직 구현 x 이기 때문에, 공지사항 리스트에서 로그인 한 것처럼 Session 값을 저장
            String userId = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ID")); //아이디
            String title = CmmUtil.nvl(request.getParameter("title")); //제목
            String communityYn = CmmUtil.nvl(request.getParameter("communityYn")); //공지글 여부
            String contents = CmmUtil.nvl(request.getParameter("contents")); //내용

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

            /*
             * 게시글을 등록하기 위한 비지니스 로직을 호출 ( 서비스에 작성한 로직 )
             *
             */
            CommunityService.insertCommunityInfo(pDTO); // INoticeService 함수를 호출함

            //저장이 완료되면 사용자에게 보여줄 메시지 작성
            msg = "등록되었습니다."; // 서비스 호출이 정상적으로 작동하면 "등록되었습니다." 메세지를 전달하기 위해 문자열 저장하기
        } catch (Exception e) { //catch 구문은 서비스 호출 중 오류가 발생되면 실행되기 때문에 "실패하였습니다." 문자열 저장
            //저장이 실패되면 사용자에게 보여줄 메세지
            msg = "실패하였습니다. : " + e.getMessage();
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


        //로그인이 구현되지 않아 USER01이라는 값을 만들어줌
        if (session.isNew()) {
            session.setAttribute("SESSION_USER_ID", "USER01");
        }

        String cSeq = CmmUtil.nvl(request.getParameter("cSeq")); // 커뮤니티글번호 pk
        String userId = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ID"));
        String title = CmmUtil.nvl((String) session.getAttribute("title"));
        String communityYn = CmmUtil.nvl((String) session.getAttribute("communityYn"));
        String contents = CmmUtil.nvl((String) session.getAttribute("contents"));

        /*
         * 로그 확인하기
         * */
        log.info("cSeq : " + cSeq);


        /*값 전달을 반드시 DTO 객체를 이용해서 처리할 전달 받은 값을 DTO 객체에 넣는다.*/
        CommunityDTO pDTO = new CommunityDTO();
        pDTO.setCommunitySeq(cSeq);
        pDTO.setUserId(userId);
        pDTO.setTitle(title);
        pDTO.setCommunityYn(communityYn);
        pDTO.setContents(contents);

        log.info("pDTO : " + pDTO.toString());

        //커뮤니티 상세정보 가져오기
        CommunityDTO rDTO = Optional.ofNullable(
                CommunityService.getCommunityInfo(pDTO, true)
        ).orElseGet(CommunityDTO::new);

        //조회된 리스트 결과값 넣어주기
        model.addAttribute("rDTO", rDTO);
//        log.info("SESSION_USER_ID" + session.getAttribute("SESSION_USER_ID"));
        model.addAttribute("SS_USER_ID",session.getAttribute("SESSION_USER_ID"));

//        log.info("rDTO : "+ rDTO);

        //모델에 세션 아이디 담아서 보내주기


        log.info(this.getClass().getName() + ".communityInfo End!");

        return "community/communityInfo";
    }

    /*게시판 수정하기
     * */
    @GetMapping(value = "communityEditInfo")
    public String communityEditInfo(HttpServletRequest request, ModelMap model) throws Exception {
        log.info(this.getClass().getName() + ".communityEditInfo Start!");

        String cSeq = CmmUtil.nvl(request.getParameter("cSeq")); //공지글번호(PK)
        String title = CmmUtil.nvl(request.getParameter("title")); //제목

        //로그 꼭 찍어주기
        log.info("cSeq : " + cSeq);
        log.info("title: "+ title);

        //DTO 객체를 이용해 전달 받은 값을 DTO 객체에 넣어주기
        CommunityDTO pDTO = new CommunityDTO();
        pDTO.setCommunitySeq(cSeq);
        pDTO.setTitle(title);

        CommunityDTO rDTO = Optional.ofNullable(
                CommunityService.getCommunityInfo(pDTO, false)
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
            String userId = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ID")); //아이디
            String cSeq = CmmUtil.nvl(request.getParameter("cSeq")); //글번호 PK
            String title = CmmUtil.nvl(request.getParameter("title")); //제목
            String communityYn = CmmUtil.nvl(request.getParameter("communityYn")); //공지글 여부
            String contents = CmmUtil.nvl(request.getParameter("contents")); //내용

            /*
             * ###################################################################
             * 반드시, 값을 받았으면, 꼭 로그를 찍어서 제대로 들어오는지 파악해야함 반드시 작성하기!!
             * */

            log.info("cSeq : " + cSeq );
            log.info("session user_id : " + userId);
            log.info("title : " + title);
            log.info("communityYn : " + communityYn);
            log.info("contents : " + contents);

            //데이터를 저장하기 위해 DTO에 값 넣어주기
            CommunityDTO pDTO = new CommunityDTO(); // 값을 넣어주기 위해 pDTO 생성
            pDTO.setUserId(userId);
            pDTO.setCommunitySeq(cSeq);
            pDTO.setTitle(title);
            pDTO.setCommunityYn(communityYn);
            pDTO.setContents(contents);

            //게시글 수정하기 DB
            CommunityService.updateCommunityInfo(pDTO);

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
    public MsgDTO communityDelete(HttpServletRequest request) {
        log.info(this.getClass().getName() + ".communityDelete Start!");

        String msg = ""; //메세지 내용
        MsgDTO dto = null; //결과 메세지 구조

        try {
            String cSeq = CmmUtil.nvl(request.getParameter("cSeq")); //글번호 (PK)

            //로그 찍어주기
            log.info("cSeq : " + cSeq);

            // 값 전달은 DTO 객체를 이용해 처리할 전달 받은 값을 DTO 객체에 넣음
            CommunityDTO pDTO = new CommunityDTO();
            pDTO.setCommunitySeq(cSeq);

            //DB에서 게시글 삭제하기
            CommunityService.deleteCommunityInfo(pDTO); //전달 받은 값을 DTO에 저장했으니 INoticeService안에 있는 delete함수를 호출해 값을 처리한다.

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
    @GetMapping(value = "about")
    public String testMain(){
        return "communityList";
    }


}
