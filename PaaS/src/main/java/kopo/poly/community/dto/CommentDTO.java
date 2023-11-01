package kopo.poly.community.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CommentDTO {

    private String rNO; //기본키 댓글번호
    private String writer; // 게시물번호
    private String content; // 댓글이 속한 댓글번호
    private String wdate; // 같은

    private String userId;

}
