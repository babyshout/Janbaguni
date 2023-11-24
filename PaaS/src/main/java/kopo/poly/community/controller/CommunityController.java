package kopo.poly.community.controller;

import kopo.poly.community.dto.CommentDTO;
import kopo.poly.community.service.ICommentService;
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
import java.util.*;

/**
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

    private final ICommentService commentService;


    /**
     * 게시판 리스트 보여주기
     * <p>
     * GetMapping(value = "community/communityList") => GET방식을 통해 접속되는 URL이 community/communityList인 경우에 아래 함수를 실행함
     */
    @GetMapping(value = "communityList")
    public String communityList(ModelMap model, @RequestParam(defaultValue = "1")int page) throws Exception {

        //로그 찍기(추후 찍은 로그를 통해 이 함수에 접근했는지 파악한다.)
        log.info(this.getClass().getName() + ".CommunityList Start!");

        List<CommunityDTO> rList = Optional.ofNullable(communityService.getCommunityList()).orElseGet(ArrayList::new);

        // 페이지당 보여줄 아이템 개수 정의
        int itemsPerPage = 5;

        // 페이지네이션을 위해 전체 아이템 개수 구하기
        int totalItems = rList.size();

        // 전체 페이지 개수 계산
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

        // 현재 페이지에 해당하는 아이템들만 선택하여 rList에 할당
        int fromIndex = (page - 1) * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, totalItems);
        rList = rList.subList(fromIndex, toIndex);


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
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);


        //실행됐는지 확인하기 위해 로그 찍어주기
        log.info(this.getClass().getName() + ".페이지 번호 : " + page);
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
    public String communityReg(HttpSession session, ModelMap model) {
        log.info(this.getClass().getName() + ".CommunityReg Start!");

        // 로그인된 사용자만 글 등록할 수 있게 설정
        String userId = (String) session.getAttribute(SessionEnum.USER_ID.STRING);

        // 사용자가 어드민이면 isAdmin을 true로 설정
        boolean isAdmin = "admin".equals(userId);
        model.addAttribute("isAdmin", isAdmin);

        log.info(this.getClass().getName() + ".CommunityReg End!");

        // 함수 처리가 끝나고 보여줄 JSP 파일명
        return "/community/communityReg";
    }


    //로그인 상태 확인 결과
    @GetMapping("/checkLoginStatus")
    @ResponseBody
    public Map<String, Object> checkLoginStatus(HttpSession session) {
        Map<String, Object> result = new HashMap<>(); //다양한 데이터 타입을 담을 수 있게 Map을 사용
        String userId = (String) session.getAttribute(SessionEnum.USER_ID.STRING);

        if (userId != null) {
            result.put("loggedIn", true); //값이 null이 아니면 true로 전달
        } else {
            result.put("loggedIn", false); //그렇지 않다면 false로 전달
        }

        return result; //result json으로 변환후 반환 ResponseBody를 통해 순수데이터 전송 후 List.html에서 ajax로 결과 처리
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

        //댓글 List를 보내줌
        List<CommentDTO> commentList = Optional.ofNullable(commentService.getCommentList(pDTO)).orElseGet(ArrayList::new);


        //리스트 값 찍어보기
        log.info("commentList Size : " + Integer.toString(commentList.size()));
//        commentList.stream().forEach(communityDTO -> {
//            log.info("List's dto : " + communityDTO.toString());
//        });


        log.info("rDTO : " + rDTO.toString());
        //조회된 리스트 결과값 넣어주기
        model.addAttribute("rDTO", rDTO);
        model.addAttribute("commentList", commentList);
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

    /**
     * 일반적으로 GetMapping이나 PostMapping 같은 경우엔 페이지를 리턴해주면
     * 템플릿 엔진을 사용하여 저장한 값을 페이지로 넘겨준다. 그렇게 되면 페이지에서 던져준 값을 활용하여 값을 사용? 할 수 있게된다.
     * <p>
     * <p>
     * <p>
     * <p>
     * ResponseBody를 사용하면 템플릿 엔진을 사용하지 않고 순수 데이터 그대로 보내주는 것이기 때문에 페이지를 리턴했을 때 오류가 나지는 않으나,
     * 던져준 값을 받지 못한다. 그러므로 페이지에서 데이터를 전달받을 수 없다?
     *
     * @param request
     * @param model
     * @return
     * @throws Exception
     * @auth 민규 수민
     */
    @ResponseBody
    @GetMapping(value = "/communitySearch")
    public List<CommunityDTO> searchKeyWord(HttpServletRequest request, ModelMap model) throws Exception {
        //String 문자열로 던져주면 Json형식으로 변환을 하지 않기 때문에 List로 던져줘야함 List<CommunityDTO>

            log.info(this.getClass().getName() + "communitySearch Start!");

            String keyWord = CmmUtil.nvl(request.getParameter("keyWord"));

            log.info("keyWord : " + keyWord);

            CommunityDTO pDTO = new CommunityDTO();
            pDTO.setKeyWord(keyWord);

            // communityService.getSearchKeyWord 메소드를 호출하여 검색 결과를 가져오기
            List<CommunityDTO> rList = Optional.ofNullable(
                    communityService.getSearchKeyWord(pDTO)
            ).orElseGet(ArrayList::new);

            log.info("rList.size() : " + rList.size());
            // TODO toString() 이 뭐하는 함수인지 확인하기!
            log.info("rList.toString() : " + rList.toString());
            log.info("rList : " + rList);
            rList.stream().forEach(communityDTO -> {
                log.info("List's dto : " + communityDTO.toString());
            });

            model.addAttribute("keyWord", keyWord );

            return rList;
    }
}
